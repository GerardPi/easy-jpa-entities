package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.CollectionDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityFieldDef;

import java.util.Optional;

import static io.github.gerardpi.easy.jpaentities.processor.JavaSourceWriter.capitalize;
import static io.github.gerardpi.easy.jpaentities.processor.JavaSourceWriter.unCapitalize;

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

    private void writeBuilderParts(JavaSourceWriter writer) {
        writeCreateAndModifyWithBuilderMethods(writer);
        writer.writeBlockBeginln("public static class Builder");
        writeBuilderFieldDeclarations(writer);
        writeFieldDeclarationWithoutPropName(new EntityFieldDef("existing", classDef.getName()), true, writer);
        writeFieldDeclarationWithoutPropName(new EntityFieldDef("id", config.getIdClass().getName()), true, writer);
        if (!classDef.isOptLockable()) {
            writeFieldDeclarationWithoutPropName(new EntityFieldDef("isNew", "boolean"), true, writer);
        }
        if (classDef.isOptLockable()) {
            writeFieldDeclarationWithoutPropName(new EntityFieldDef("optLockVersion", Integer.class.getName()), true, writer);
        }
        writer.emptyLine()
                .writeBlockBeginln("private Builder(" + config.getIdClass().getName() + " id)")
                .writeLine("this.id = java.util.Objects.requireNonNull(id);")
                .writeLine("this.existing = null;");

        if (!classDef.isOptLockable()) {
            writer.writeLine("this.isNew = true;");
        }

        if (classDef.isOptLockable()) {
            writer.writeLine("this.optLockVersion = null;");
        }

        writer
                .writeAssignmentsToNull(classDef.getFieldDefs())
                .writeBlockEnd()
                .emptyLine()
                .writeBlockBeginln("private Builder(" + classDef.getName() + " existing)")
                .writeLine("this.existing = java.util.Objects.requireNonNull(existing);")
                .writeLine("this.id = existing.getId();");

        if (!classDef.isOptLockable()) {
            writer.writeLine("this.isNew = false;");
        }

        if (classDef.isOptLockable()) {
            writer
                    .writeLine("this.optLockVersion = existing.getOptLockVersion();");
        }
        writeAssignmentsInBuilderConstructor("this.", "existing.", writer);
        writer.writeBlockEnd();
        writer.emptyLine();
        writeBuilderSetters(writer);
        writeBuilderBuildMethod(writer);
        writer.writeBlockEnd();
    }
    private void writeBuilderSetters(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> {
            writer.writeBlockBeginln("public Builder set" + capitalize(fieldDef.getName()) + "(" + fieldDef.getType() + " " + fieldDef.getName() + ")");
            writer.assign("this.", fieldDef.getName(), "", fieldDef.getName());
            writer.writeLine("return this;");
            writer.writeBlockEnd();
            fieldDef.fetchCollectionDef()
                    .ifPresent(collectionDef -> writeBuilderAddToCollection(fieldDef, collectionDef, writer));
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
        classDef.getFieldDefs().forEach(fieldDef -> builderAssign(assigneePrefix, assignedValuePrefix, fieldDef,  writer ));
    }

    private void builderAssign(String assigneePrefix, String assignedFieldPrefix, EntityFieldDef entityFieldDef, JavaSourceWriter writer) {
        writer.writeLine(assigneePrefix + entityFieldDef.getName() + " = " + assignedFieldPrefix + entityFieldDef.getName() + ";");
            /*
        if (entityFieldDef.isCollection()) {
            writeCollectionToBuilders(assigneePrefix, assignedFieldPrefix, entityFieldDef, writer);
            /*
            if (config.getEntityClassDefNames().contains(entityFieldDef.getCollectedType())) {
                writeCollectionToBuilders(assigneePrefix, assignedFieldPrefix, entityFieldDef, writer);
            } else {
                String assignee = assigneePrefix + entityFieldDef.getName() + "Builders";
                writer.writeLine(assignee + " = " + writer.immutable(assignedFieldPrefix, entityFieldDef, false) + ";");
            }
        } else {
            writer.writeLine(assigneePrefix + entityFieldDef.getName() + " = " + assignedFieldPrefix + entityFieldDef.getName() + ";");
        }
            */
    }

    void writeCollectionToBuilders(String assigneePrefix, String assignedFieldPrefix, EntityFieldDef entityFieldDef, JavaSourceWriter writer) {
        String assignee = unCapitalize(entityFieldDef.getCollectedType()) + "Builders";
        String targetCollections = entityFieldDef.getCollectionDef().getCollectionClassSimpleName();
        writer.writeLine(assigneePrefix + assignee + " = " + assignedFieldPrefix + entityFieldDef.getName() +
                String.format(".stream().map(e -> e.modify()).collect(java.util.stream.Collectors.to%s());", targetCollections));
    }

    private void writeBuilderFieldDeclarations(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef ->
                writeFieldDeclarationWithoutPropName(fieldDef, false, writer)
        );
    }

    private void writeCreateAndModifyWithBuilderMethods(JavaSourceWriter writer) {
        writer.writeBlockBeginln("public static Builder create(" + config.getIdClass().getName() + " id)")
                .writeLine("return new Builder(id);")
                .writeBlockEnd()
                .emptyLine()
                .writeBlockBeginln("public Builder modify()")
                .writeLine("return new Builder(this);")
                .writeBlockEnd();
    }

    private void writeFieldDeclarationWithoutPropName(EntityFieldDef entityFieldDef, boolean isFinal, JavaSourceWriter writer) {
        /*
            Optional<CollectionDef> optCollectionDef = entityFieldDef.fetchCollectionDef();
            if (optCollectionDef.isPresent()) {
                CollectionDef collectionDef = optCollectionDef.get();
                if (config.getEntityClassDefNames().contains(collectionDef.getCollectedType())) {
                    String type = collectionDef.getCollectionType() + "<" + collectionDef.getCollectedType() + "." + "Builder>";
                    String name = unCapitalize(collectionDef.getCollectedType()) + "Builders";
                    writer.writeLine("private final " + type + " " + name + ";");
                }
            } else {
                String prefix = isFinal ? "private final " : "private ";
                writer.writeLine(prefix + entityFieldDef.getType() + " " + entityFieldDef.getName() + ";");
            }
            */
        String prefix = isFinal ? "private final " : "private ";
        writer.writeLine(prefix + entityFieldDef.getType() + " " + entityFieldDef.getName() + ";");
    }
}
