package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityFieldDef;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.gerardpi.easy.jpaentities.processor.JavaSourceWriter.THIS_PREFIX;
import static io.github.gerardpi.easy.jpaentities.processor.JavaSourceWriter.capitalize;

public class EntityClassGenerator {
    private final EntityClassDef classDef;
    private final EntityClassBuilderGenerator entityClassBuilderGenerator;
    private final EasyJpaEntitiesConfig config;

    public EntityClassGenerator(EntityClassDef classDef, EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        this.config = easyJpaEntitiesConfig;
        this.classDef = classDef;
        this.entityClassBuilderGenerator = new EntityClassBuilderGenerator(classDef, easyJpaEntitiesConfig);
    }

    public void write(JavaSourceWriter writer) {
        writeClassHeader(writer);
        if (classDef.isReadOnly()) {
            writer.writeLine("@org.hibernate.annotations.Immutable");
        }
        writeClassDeclaration(writer);
        writeEntityFieldDeclarations(writer);
        writeConstructors(writer);
        writeFieldGetters(writer);
        writeToStringMethod(writer);

        if (!classDef.isReadOnly()) {
            entityClassBuilderGenerator.write(writer);
        }

        writer.writeBlockEnd();
    }

    private void writeClassHeader(JavaSourceWriter writer) {
        writer.writeLine("package " + config.getTargetPackage() + ";")
                .emptyLine()
                .writeLine("// Generated date/time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .writeLine("@javax.persistence.Entity")
                .writeLine("@javax.persistence.Access(javax.persistence.AccessType.FIELD)");
    }

    private void writeClassDeclaration(JavaSourceWriter writer) {
        String extendsPart = classDef.getExtendsFromClass() == null ? "" : " extends " + classDef.getExtendsFromClass();
        writer.writeBlockBeginln("class " + classDef.getName() + extendsPart);
    }

    private void writeEntityFieldDeclarations(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> writeFieldDeclaration(fieldDef, writer));
    }

    private void writeFieldDeclaration(EntityFieldDef entityFieldDef, JavaSourceWriter writer) {
        writer.writeLine("public static final String PROPNAME_" + entityFieldDef.getName().toUpperCase() + " = " + writer.quoted(entityFieldDef.getName()) + ";");
        entityFieldDef.getAnnotations().forEach(annotation -> writer.writeLine("@" + annotation));
        writer.writeLine("private final " + entityFieldDef.getType() + " " + entityFieldDef.getName() + ";");
    }

    private void writeConstructors(JavaSourceWriter writer) {
        writeDefaultConstructor(writer);
        if (config.isIncludeConstructorWithParameters()) {
            if (classDef.isReadOnly()) {
                throw new IllegalStateException("A constructor with parameters must be include, but class is readOnly.");
            }
            writeConstructor(writer);
        }
        if (!classDef.isReadOnly()) {
            writeConstructorUsingBuilder(writer);
        }
    }

    private void writeAssignmentsInConstructor(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> writer
                .assign("this.", "builder.", fieldDef, true));
    }

    private void writeConstructorUsingBuilder(JavaSourceWriter writer) {
        writer
                .writeBlockBeginln(classDef.getName() + "(Builder builder)")
                .writeLine(classDef.isOptLockable()
                        ? "super(builder.id, builder.optLockVersion, builder.isModified);"
                        : "super(builder.id, builder.isPersisted, builder.isModified);");
        writeAssignmentsInConstructor(writer);
        writer.writeBlockEnd();
    }

    private void writeDefaultConstructor(JavaSourceWriter writer) {
        writer.writeBlockBeginln(classDef.getName() + "()");
        classDef.getFieldDefs().forEach(writer::assignNull);
        writer.writeBlockEnd();
    }

    private void writeConstructor(JavaSourceWriter writer) {
        writer.writeBlockBeginln("private " + classDef.getName() + "(" + methodParameterDeclarations() + ")");
        classDef.getFieldDefs().forEach(fieldDef -> writer.assign(THIS_PREFIX, "",  fieldDef, true));
        writer.writeBlockEnd();
    }

    private String methodParameterDeclarations() {
        return classDef.getFieldDefs().stream()
                .map(fieldDef -> fieldDef.getType() + " " + fieldDef.getName())
                .collect(Collectors.joining(", "));
    }

    private void writeFieldGetters(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> {
            writer.writeMethodSignature(fieldDef.getType(), "get" + capitalize(fieldDef.getName()))
                    .writeLine("return " + fieldDef.getName() + ";")
                    .writeBlockEnd();
        });
    }

    private void writeToStringMethod(JavaSourceWriter writer) {
        writer
                .writeLine("@Override")
                .writeBlockBeginln("public String toString()")
                .writeLine("return " + writer.quoted("class=") + " + this.getClass().getName()")
                .incIndentation()
                .writeLine("+ " + writer.quoted(";id=") + "+ this.getId()");

        if (classDef.isOptLockable()) {
            writer.writeLine("+ " + writer.quoted(";optLockVersion=") + " + this.getOptLockVersion()");
        }
        writer.writeLine("+ " + writer.quoted(";isModified=") + "+ this.isModified()");
        classDef.getFieldDefs().forEach(fieldDef -> {
            writer.writeLine("+ " + writer.quoted(";" + fieldDef.getName() + "=") + " + this." + fieldDef.getName());
        });
        writer.writeLine(";")
                .decIndentation()
                .writeBlockEnd();
    }
}
