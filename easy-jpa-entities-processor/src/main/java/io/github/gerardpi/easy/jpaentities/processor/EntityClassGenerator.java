package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.CollectionDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityFieldDef;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.gerardpi.easy.jpaentities.processor.JavaSourceWriter.THIS_PREFIX;

public class EntityClassGenerator {
    private final EntityClassDef classDef;
    private final String targetPackage;
    private final boolean includeConstructorWithParameters;
    private final Class<?> idClass;
    private final List<EntityClassDef> entityClassDefs;
    private final EntityClassBuilderGenerator entityClassBuilderGenerator;

    public EntityClassGenerator(EntityClassDef classDef, EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        this.classDef = classDef;
        this.targetPackage = easyJpaEntitiesConfig.getTargetPackage();
        this.includeConstructorWithParameters = easyJpaEntitiesConfig.isIncludeConstructorWithParameters();
        this.entityClassDefs = easyJpaEntitiesConfig.getEntityClassDefs();
        this.idClass = easyJpaEntitiesConfig.getIdClass();
        this.entityClassBuilderGenerator = new EntityClassBuilderGenerator(classDef, easyJpaEntitiesConfig);
    }

    public void write(JavaSourceWriter writer) {
        writer.writePackageLine(targetPackage)
                .emptyLine()
                .writeLine("// Generated date/time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .writeLine("@javax.persistence.Entity")
                .writeLine("@javax.persistence.Access(javax.persistence.AccessType.FIELD)");
        if (classDef.isReadOnly()) {
            writer.writeLine("@org.hibernate.annotations.Immutable");
        }
        writeClassDeclaration(writer);
        writeEntityFieldDeclarations(writer);
        writeConstructors(includeConstructorWithParameters, writer);
        writeFieldGetters(writer);
        writeToStringMethod(writer);

        if (!classDef.isReadOnly()) {
            entityClassBuilderGenerator.write(writer);
        }

        writer.writeBlockEnd();
    }

    private void writeClassDeclaration(JavaSourceWriter writer) {
        String extendsPart = classDef.getExtendsFromClass() == null ? "" : " extends " + classDef.getExtendsFromClass();
        writer.writeBlockBeginln("class " + classDef.getName() + extendsPart);
    }

    private void writeEntityFieldDeclarations(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> writeFieldDeclaration(fieldDef, false, writer));
    }

    private void writeFieldDeclaration(EntityFieldDef entityFieldDef, boolean isFinal, JavaSourceWriter writer) {
        writer.writeLine("public static final String PROPNAME_" + entityFieldDef.getName().toUpperCase() + " = " + writer.quoted(entityFieldDef.getName()) + ";");
        entityFieldDef.getAnnotations().forEach(annotation -> writer.writeLine("@" + annotation));
        String prefix = isFinal ? "private final " : "private ";
        writer.writeLine(prefix + entityFieldDef.getType() + " " + entityFieldDef.getName() + ";");
    }

    private void writeConstructors(boolean includeConstructorWithParameters, JavaSourceWriter writer) {
        writeDefaultConstructor(writer);
        if (includeConstructorWithParameters) {
            if (classDef.isReadOnly()) {
                throw new IllegalStateException("A constructor with parameters must be include, but class is readOnly.");
            }
            writeConstructor(writer);
        }
//        if (!classDef.isReadOnly()) {
//            writeConstructorUsingBuilder(classDef);
//        }
    }

    private void writeDefaultConstructor(JavaSourceWriter writer) {
        writer.writeBlockBeginln(classDef.getName() + "()");
        writer.writeAssignmentsToNull(classDef.getFieldDefs());
        writer.writeBlockEnd();
    }

    private void writeConstructor(JavaSourceWriter writer) {
        writer.writeBlockBeginln("private " + classDef.getName() + "(" + methodParameterDeclarations() + ")");
        writer.writeAssignmentsInConstructor(classDef.getFieldDefs(), THIS_PREFIX, "");
        writer.writeBlockEnd();
    }

    private String methodParameterDeclarations() {
        return classDef.getFieldDefs().stream()
                .map(fieldDef -> fieldDef.getType() + " " + fieldDef.getName())
                .collect(Collectors.joining(", "));
    }

    private void writeFieldGetters(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> {
            writer.writeMethodSignature(fieldDef.getType(), "get" + writer.capitalize(fieldDef.getName()));
            writer.writeLine("return " + fieldDef.getName() + ";");
            writer.writeBlockEnd();
        });
    }

    private void writeToStringMethod(JavaSourceWriter writer) {
        writer.writeLine("@Override");
        writer.writeBlockBeginln("public String toString()");
        writer.writeLine("return " + writer.quoted("class=") + " + this.getClass().getName()");
        writer.incIndentation();
        writer.writeLine("+ " + writer.quoted(";id=") + "+ this.getId()");

        if (!classDef.isOptLockable()) {
            writer.writeLine("+ " + writer.quoted(";optLockVersion=") + " + this.getOptLockVersion()");
        }
        classDef.getFieldDefs().forEach(fieldDef -> {
            writer.writeLine("+ " + writer.quoted(";" + fieldDef.getName() + "=") + " + this." + fieldDef.getName());
        });
        writer.writeLine(";");
        writer.decIndentation();
        writer.writeBlockEnd();
    }
}
