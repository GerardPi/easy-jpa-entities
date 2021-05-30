package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.CollectionDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityFieldDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.gerardpi.easy.jpaentities.processor.JavaSourceWriter.capitalize;

public class EntityClassBuilderGenerator {
    private final EntityClassDef classDef;
    private final EasyJpaEntitiesConfig config;

    public EntityClassBuilderGenerator(EntityClassDef classDef, EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        this.classDef = classDef;
        this.config = easyJpaEntitiesConfig;
    }

    public void write(JavaSourceWriter writer) {
        writeBuilderParts(writer);
    }

    private String getCreationParameters() {
        List<String> constructorParameters = new ArrayList<>();
        if (classDef.isEntity()) {
            constructorParameters.add(config.getIdClass().getName() + " id");
        }
        constructorParameters.addAll(classDef.getFieldDefs().stream()
                .filter(EntityFieldDef::isWriteOnce)
                .map(fieldDef -> fieldDef.getType() + " " + fieldDef.getName()).collect(Collectors.toList()));
        return String.join(", ", constructorParameters);
    }

    private String getCreationArguments() {
        List<String> constructorParameters = new ArrayList<>();
        if (classDef.isEntity()) {
            constructorParameters.add("id");
        }
        constructorParameters.addAll(classDef.getFieldDefs().stream()
                .filter(EntityFieldDef::isWriteOnce)
                .map(EntityFieldDef::getName).collect(Collectors.toList()));
        return String.join(", ", constructorParameters);
    }

    private void writeBuilderConstructorForNew(JavaSourceWriter writer) {

        writer.emptyLine()
                .writeBlockBeginln("private Builder(" + getCreationParameters() + ")");
        if (classDef.isEntity()) {
            writer.writeLine("this.id = java.util.Objects.requireNonNull(id);")
                    .writeLine("this.isModified = false;")
                    .writeLine("this.existing = null;");
        }

        if (classDef.isOptLockable()) {
            writer.writeLine("this.optLockVersion = null;");
        } else if (classDef.isPersistable()) {
            writer.writeLine("this.isPersisted = false;");
        }

        writer
                .writeAssignmentsToNull(classDef.getFieldDefs())
                .writeBlockEnd();
    }

    private void writeBuilderParts(JavaSourceWriter writer) {
        writeCreateAndModifyWithBuilderMethods(writer);
        writer.writeBlockBeginln("public static class Builder");
        writeBuilderFieldDeclarations(writer);
        if (classDef.isEntity()) {
            writeFieldDeclaration(createFieldDef("existing", classDef.getName()), true, writer);
            writeFieldDeclaration(createFieldDef("id", config.getIdClass().getName()), true, writer);
            if (!classDef.isOptLockable()) {
                writeFieldDeclaration(createFieldDef("isPersisted", "boolean"), true, writer);
            } else {
                writeFieldDeclaration(createFieldDef("optLockVersion", Integer.class.getName()), true, writer);
            }
            writeFieldDeclaration(createFieldDef("isModified", "boolean"), false, writer);
        }
        writeBuilderConstructorForNew(writer);
        writeBuilderCopyConstructor(writer);
        writer.emptyLine();
        writeBuilderSetters(writer);
        writeBuilderBuildMethod(writer);
        writer.writeBlockEnd();
    }

    private EntityFieldDef createFieldDef(String name, String type) {
        return new EntityFieldDef.Builder(name, null, type, null, Collections.emptyList(), false, false).build();
    }

    private void writeBuilderCopyConstructor(JavaSourceWriter writer) {
        writer
                .emptyLine()
                .writeBlockBeginln("private Builder(" + classDef.getName() + " existing)");
        if (classDef.isEntity()) {
            writer.writeLine("this.existing = java.util.Objects.requireNonNull(existing);")
                    .writeLine("this.isModified = false;")
                    .writeLine("this.id = existing.getId();");
        }

        if (classDef.isPersistable()) {
            writer.writeLine("this.isPersisted = true;");
        }

        if (classDef.isOptLockable()) {
            writer
                    .writeLine("this.optLockVersion = existing.getOptLockVersion();");
        }
        writeAssignmentsInBuilderConstructor("this.", "existing.", writer);
        writer.writeBlockEnd();
    }

    private void writeBuilderSetters(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> {
            if (!fieldDef.isWriteOnce()) {
                writer.writeBlockBeginln("public Builder set" + capitalize(fieldDef.getName()) + "(" + fieldDef.getType()
                        + " " + fieldDef.getName() + ")");
                writer.assign("this.", fieldDef.getName(), "", fieldDef.getName());
                if (classDef.isEntity()) {
                    writer.assign("this.", "isModified", "", "true");
                }
                writer.writeLine("return this;");
                writer.writeBlockEnd();
                fieldDef.fetchCollectionDef()
                        .ifPresent(collectionDef -> writeBuilderAddToCollection(fieldDef, collectionDef, writer));
            }
        });
    }

    private void writeBuilderBuildMethod(JavaSourceWriter writer) {
        writer.writeBlockBeginln("public " + classDef.getName() + " build()")
                .writeLine("return new " + classDef.getName() + "(this);")
                .writeBlockEnd();
    }


    void writeBuilderAddToCollection(EntityFieldDef fieldDef, CollectionDef collectionDef, JavaSourceWriter writer) {
        writer
                .writeLine("/**")
                .writeLine(" * CAUTION: If the entity used to create the builder already contained this collection")
                .writeLine(" * then that collection probably is immutable.")
                .writeLine(" * Before using this add... method, first replace it with a mutable copy using the setter.")
                .writeLine(" * and only then use this add... method.")
                .writeLine(" * If the collection contains nested objects, you probably want to create some algorithm")
                .writeLine(" * specifically to make it possible to manipulate it and then use the setter to put it into the builder.")
                .writeLine(" */")
                .writeBlockBeginln("public Builder add" + capitalize(fieldDef.getSingular()) + ("(" + collectionDef.getCollectedType() + " " + fieldDef.getSingular() + ")"))
                .writeBlockBeginln("if (this." + fieldDef.getName() + " == null)")
                .writeLine("this." + fieldDef.getName() + " = new " + collectionDef.getCollectionImplementationType() + "<>();")
                .writeBlockEnd()
                .writeLine("this." + fieldDef.getName() + ".add(" + fieldDef.getSingular() + ");")
                .writeLine("return this;")
                .writeBlockEnd();
    }

    private void writeAssignmentsInBuilderConstructor(String assigneePrefix, String assignedValuePrefix, JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> builderAssign(assigneePrefix, assignedValuePrefix, fieldDef, writer));
    }

    private void builderAssign(String assigneePrefix, String assignedFieldPrefix, EntityFieldDef entityFieldDef, JavaSourceWriter writer) {
        writer.writeLine(assigneePrefix + entityFieldDef.getName() + " = " + assignedFieldPrefix + entityFieldDef.getName() + ";");
    }

    private void writeBuilderFieldDeclarations(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef ->
                writeFieldDeclaration(fieldDef, fieldDef.isWriteOnce(), writer)
        );
    }

    private void writeCreateAndModifyWithBuilderMethods(JavaSourceWriter writer) {

        writer.writeBlockBeginln("public static Builder create(" + getCreationParameters() + ")")
                .writeLine("return new Builder(" + getCreationArguments() + ");")
                .writeBlockEnd()
                .emptyLine()
                .writeBlockBeginln("public Builder modify()")
                .writeLine("return new Builder(this);")
                .writeBlockEnd();
    }

    private void writeFieldDeclaration(EntityFieldDef entityFieldDef, boolean isFinal, JavaSourceWriter writer) {
        String prefix = isFinal ? "private final " : "private ";
        writer.writeLine(prefix + entityFieldDef.getType() + " " + entityFieldDef.getName() + ";");
    }
}
