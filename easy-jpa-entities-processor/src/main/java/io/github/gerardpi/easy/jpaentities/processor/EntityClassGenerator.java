package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityFieldDef;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.gerardpi.easy.jpaentities.processor.JavaSourceWriter.THIS_PREFIX;
import static io.github.gerardpi.easy.jpaentities.processor.JavaSourceWriter.capitalize;

public class EntityClassGenerator {
    private final EntityClassDef classDef;
    private final EntityClassBuilderGenerator entityClassBuilderGenerator;
    private final EasyJpaEntitiesConfig config;
    private final boolean forDtoClasses;

    public EntityClassGenerator(EntityClassDef classDef, EasyJpaEntitiesConfig easyJpaEntitiesConfig, boolean forDtoClasses) {
        this.config = easyJpaEntitiesConfig;
        this.classDef = classDef;
        this.entityClassBuilderGenerator = new EntityClassBuilderGenerator(classDef, easyJpaEntitiesConfig, forDtoClasses);
        this.forDtoClasses = forDtoClasses;
    }

    public EntityClassGenerator(EntityClassDef classDef, EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        this(classDef, easyJpaEntitiesConfig, false);
    }

    public void write(JavaSourceWriter writer) {
        writeClassHeader(writer);
        if (classDef.isReadOnly()) {
            writer.writeLine("@org.hibernate.annotations.Immutable");
        }
        writeSuppressWarningNonNullValuesShouldNotBeSetToNull(writer);
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

    /**
     * Code quality scanners will report that, for persistence purposes,
     * fields are marked to never be null,
     * whilst the default constructor (required by ORM framework)
     * will initially set fields to null.
     */
    private void writeSuppressWarningNonNullValuesShouldNotBeSetToNull(JavaSourceWriter writer) {
        writer.writeLine("@SuppressWarnings(\"java:S2637\")");
    }

    private void writeClassHeader(JavaSourceWriter writer) {
        writer.writeLine("package " + getTargetPath() + ";")
                .emptyLine();
        if (config.isIncludeCommentWithTimestamp()) {
            writer.writeLine("// Generated")
                    .writeLine("//         date/time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                    .writeLine("//         details: https://github.com/GerardPi/easy-jpa-entities");
        }

        writeImports(writer);
        if (isForEntity()) {
            writer
                    .writeLine("@javax.persistence.Entity")
                    .writeLine("@javax.persistence.Access(javax.persistence.AccessType.FIELD)");
            classDef.getAnnotations().forEach(annotation -> writer.writeLine("@" + annotation));
        }
    }
    private void writeImports(JavaSourceWriter writer) {
        if (forDtoClasses) {
            writer.writeImport(config.getTargetPackage(), getSuperclass(true));
            writer.writeImport(config.getTargetPackage(), getSuperclass(false));
        }
    }

    private String getSuperclass(boolean forDtoClas) {
        return classDef.getSuperClass(forDtoClas)
                .orElseThrow(() -> new IllegalStateException("Expected a superclass to be available for "
                        + EntityClassDef.class.getSimpleName() + classDef));
    }

    private boolean isForEntity() {
        return classDef.isEntity() && !forDtoClasses;
    }

    private String getTargetPath() {
        if (forDtoClasses) {
            return classDef.getDtoTargetPackage().orElseThrow(() -> new IllegalStateException("No DTO class target path was provided"));
        }
        return config.getTargetPackage();
    }


    private void writeClassDeclaration(JavaSourceWriter writer) {
        String extendsPart = classDef.getSuperClass(forDtoClasses)
                .map(superClass -> " extends " + superClass)
                .orElse(" implements java.io.Serializable");
        writer.writeBlockBeginln("public class " + getClassName() + extendsPart);
    }


    private String getClassName() {
        return classDef.getName() + (forDtoClasses ? "Dto" : "");
    }

    private void writeEntityFieldDeclarations(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> writeFieldDeclaration(fieldDef, writer));
    }

    private void writeFieldDeclaration(EntityFieldDef entityFieldDef, JavaSourceWriter writer) {
        writer.writeLine("public static final String PROPNAME_" + entityFieldDef.getName().toUpperCase() + " = " + writer.quoted(entityFieldDef.getName()) + ";");
        if (isForEntity()) {
            entityFieldDef.getAnnotations().forEach(annotation -> writer.writeLine("@" + annotation));
        }
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
        if (forDtoClasses) {
            writeJacksonConstructor(writer);
        }
    }

    private void writeAssignmentsInConstructor(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> writer
                .assign("this.", "builder.", fieldDef, true));
    }

    private void writeConstructorUsingBuilder(JavaSourceWriter writer) {
        writer.writeBlockBeginln(getClassName() + "(Builder builder)");
        if (classDef.hasTag()) {
            if (forDtoClasses) {
                writer.writeLine("super(builder.id, builder.etag);");
            } else {
                writer.writeLine("super(builder.id, builder.etag, builder.isModified);");
            }
        } else if (classDef.isPersistableEntity()) {
            if (forDtoClasses) {
                writer.writeLine("super(builder.id);");
            } else {
                writer.writeLine("super(builder.id, builder.isPersisted, builder.isModified);");
            }
        }
        writeAssignmentsInConstructor(writer);
        writer.writeBlockEnd();
    }

    private void writeDefaultConstructor(JavaSourceWriter writer) {
        writer.writeBlockBeginln(getClassName() + "()");
        classDef.getFieldDefs().forEach(writer::assignNull);
        writer.writeBlockEnd();
    }

    private void writeConstructor(JavaSourceWriter writer) {
        writer.writeBlockBeginln("private " + getClassName() + "(" + methodParameterDeclarations() + ")");
        classDef.getFieldDefs().forEach(fieldDef -> writer.assign(THIS_PREFIX, "", fieldDef, true));
        writer.writeBlockEnd();
    }

    private void writeJacksonConstructor(JavaSourceWriter writer) {
        writer.writeLine("@com.fasterxml.jackson.annotation.JsonCreator");
        writer.writeBlockBeginln("public " + getClassName() + "(" + methodParameterDeclarationsForJackson() + ")");
        writer.writeLine("super(id" + (classDef.hasTag() ? ", etag" : "") + ");");
        classDef.getFieldDefs().forEach(fieldDef -> writer.assign(THIS_PREFIX, "", fieldDef, true));
        writer.writeBlockEnd();
    }


    private String methodParameterDeclarations() {
        return classDef.getFieldDefs().stream()
                .map(fieldDef -> fieldDef.getType() + " " + fieldDef.getName())
                .collect(Collectors.joining(", "));
    }

    private List<EntityFieldDef> getFieldDefsForJackson() {
        List<EntityFieldDef> fieldDefs = new ArrayList<>();
        fieldDefs.add(EntityFieldDef.create().setType(config.getIdClass().getName()).setName("id").build());
        if (classDef.hasTag()) {
            fieldDefs.add(EntityFieldDef.create().setType(java.lang.String.class.getName()).setName("etag").build());
        }
        fieldDefs.addAll(classDef.getFieldDefs());
        return Collections.unmodifiableList(fieldDefs);
    }

    private String methodParameterDeclarationsForJackson() {
        if (config.isDtoWithJsonPropertyAnnotations()) {
            return getFieldDefsForJackson().stream()
                    .map(fieldDef -> "@com.fasterxml.jackson.annotation.JsonProperty(\"" + fieldDef.getName() + "\") " + fieldDef.getType() + " " + fieldDef.getName())
                    .collect(Collectors.joining(", "));
        } else {
            return getFieldDefsForJackson().stream()
                    .map(fieldDef -> fieldDef.getType() + " " + fieldDef.getName())
                    .collect(Collectors.joining(", "));

        }
    }

    private void writeFieldGetters(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> {
            writeMethodSignature(fieldDef.getType(), "get" + capitalize(fieldDef.getName()), writer);
            writer
                    .writeLine("return " + fieldDef.getName() + ";")
                    .writeBlockEnd();
        });
    }

    private void writeToStringMethod(JavaSourceWriter writer) {
        writer
                .writeLine("@Override")
                .writeBlockBeginln("public String toString()")
                .writeLine("return " + writer.quoted("class=") + " + this.getClass().getName()")
                .incIndentation();

        if (classDef.isIdentifiable()) {
            writer.writeLine("+ " + writer.quoted(";id=") + "+ this.getId()");
            if (!forDtoClasses) {
                writer.writeLine("+ " + writer.quoted(";isModified=") + "+ this.isModified()");
            }
        }
        if (classDef.hasTag()) {
            writer.writeLine("+ " + writer.quoted(";etag=") + " + this.getEtag()");
        }
        classDef.getFieldDefs()
                .forEach(fieldDef -> writer.writeLine("+ " + writer.quoted(";" + fieldDef.getName() + "=") + " + this." + fieldDef.getName()));

        writer.writeLine(";")
                .decIndentation()
                .writeBlockEnd();
    }


    private String methodParameterDeclarations(List<EntityFieldDef> fieldDefs) {
        return fieldDefs.stream()
                .map(fieldDef -> fieldDef.getType() + " " + fieldDef.getName())
                .collect(Collectors.joining(", "));
    }

    private void writeMethodSignature(String type, String methodName, JavaSourceWriter writer) {
        writeMethodSignature(type, methodName, Collections.emptyList(), writer);
    }

    private void writeMethodSignature(String type, String methodName, List<EntityFieldDef> fieldDefs, JavaSourceWriter writer) {
        writer.writeBlockBeginln("public " + type + " " + methodName + " (" + methodParameterDeclarations(fieldDefs) + ")");
    }

}
