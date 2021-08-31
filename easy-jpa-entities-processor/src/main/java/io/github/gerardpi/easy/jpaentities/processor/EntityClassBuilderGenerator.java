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
    public static final String PRIVATE_BUILDER = "private Builder(";
    public static final String IS_MODIFIED = "isModified";
    public static final String ASSIGNEE_PREFIX_THIS = "this.";
    public static final String RETURN_THIS = "return this;";
    private final EntityClassDef classDef;
    private final EasyJpaEntitiesConfig config;
    private final boolean forDtoClasses;

    public EntityClassBuilderGenerator(EntityClassDef classDef, EasyJpaEntitiesConfig easyJpaEntitiesConfig, boolean forDtoClasses) {
        this.classDef = classDef;
        this.config = easyJpaEntitiesConfig;
        this.forDtoClasses = forDtoClasses;
    }

    public void write(JavaSourceWriter writer) {
        writeBuilderParts(writer);
    }

    private String getClassName() {
        return classDef.getName() + (forDtoClasses ? "Dto" : "");
    }

    private boolean isForEntity() {
        return classDef.isEntity() && !forDtoClasses;
    }

    private String getCreationParameters() {
        List<String> constructorParameters = new ArrayList<>();
        if (isForEntity()) {
            constructorParameters.add(config.getIdClass().getName() + " id");
        }
        constructorParameters.addAll(classDef.getFieldDefs().stream()
                .filter(EntityFieldDef::isWriteOnce)
                .map(fieldDef -> fieldDef.getType() + " " + fieldDef.getName()).collect(Collectors.toList()));
        return String.join(", ", constructorParameters);
    }

    private String getCreationArguments() {
        List<String> constructorParameters = new ArrayList<>();
        if (isForEntity()) {
            constructorParameters.add("id");
        }
        constructorParameters.addAll(classDef.getFieldDefs().stream()
                .filter(EntityFieldDef::isWriteOnce)
                .map(EntityFieldDef::getName).collect(Collectors.toList()));
        return String.join(", ", constructorParameters);
    }

    private void writeBuilderConstructorForNew(JavaSourceWriter writer) {

        writer.emptyLine()
                .writeBlockBeginln(PRIVATE_BUILDER + getCreationParameters() + ")");
        if (classDef.isIdentifiable()) {
            if (forDtoClasses) {
                writer.writeLine("this.id = null;");
            } else {
                writer.writeLine("this.id = java.util.Objects.requireNonNull(id);")
                        .writeLine("this.isModified = false;");
            }

            if (classDef.hasTag()) {
                writer.writeLine("this.etag = null;");
            } else if (classDef.isPersistableEntity()) {
                writer.writeLine("this.isPersisted = false;");
            }
        }

        writer
                .writeAssignmentsToNull(classDef.getFieldDefs())
                .writeBlockEnd();
    }

    private void writeBuilderParts(JavaSourceWriter writer) {
        writeCreateAndModifyWithBuilderMethods(writer);
        writeCreateFromPersistableEntity(writer);
        writer.writeBlockBeginln("public static class Builder");
        writeBuilderFieldDeclarations(writer);
        if (classDef.isIdentifiable()) {
            writeFieldDeclaration(createFieldDef("id", config.getIdClass().getName()), true, writer);
        }
        if (classDef.isEntity()) {
            if (classDef.hasTag()) {
                if (forDtoClasses) {
                    writeFieldDeclaration(createFieldDef("etag", String.class.getName()), true, writer);
                } else {
                    writeFieldDeclaration(createFieldDef("etag", Integer.class.getName()), true, writer);
                }
            }
            if (!forDtoClasses) {
                if (classDef.isPersistableEntity()) {
                    writeFieldDeclaration(createFieldDef("isPersisted", "boolean"), true, writer);
                }
                writeFieldDeclaration(createFieldDef(IS_MODIFIED, "boolean"), false, writer);
            }
        }

        writeBuilderConstructorForNew(writer);
        writeBuilderCopyConstructor(writer, getClassName());

        if (forDtoClasses) {
            writeBuilderCopyConstructor(writer, config.getTargetPackage() + "." + classDef.getName());
        }
        writeBuilderCopyConstructorFromEntity(writer);
        writer.emptyLine();
        writeBuilderSetters(writer);
        if (!forDtoClasses) {
            writeBuilderIfNotNullSetters(writer);
        }
        writeBuilderBuildMethod(writer);
        writer.writeBlockEnd();
    }


    private EntityFieldDef createFieldDef(String name, String type) {
        return new EntityFieldDef.Builder(name, null, type, null, Collections.emptyList(), false, false).build();
    }

    private void writeBuilderCopyConstructor(JavaSourceWriter writer, String classNameCopySource) {
        writer
                .emptyLine()
                .writeBlockBeginln(PRIVATE_BUILDER + classNameCopySource + " existing)");
        if (classDef.isEntity()) {
            writer.writeLine("this.id = existing.getId();");
            if (classDef.hasTag()) {
                if (forDtoClasses) {
                    writer.writeLine("this.etag = \"\" + existing.getEtag();");
                } else {
                    writer.writeLine("this.etag = existing.getEtag();");
                }
            } else if (classDef.isPersistableEntity()) {
                writer.writeLine("this.isPersisted = true;");
            }
            if (!forDtoClasses) {
                writer.writeLine("this.isModified = false;");
            }
        }
        if (forDtoClasses) {
            writeAssignmentsInBuilderConstructorUsingGetters(ASSIGNEE_PREFIX_THIS, "existing.", writer);
        } else {
            writeAssignmentsInBuilderConstructorUsingFields(ASSIGNEE_PREFIX_THIS, "existing.", writer);
        }
        writer.writeBlockEnd();
    }

    private void writeBuilderCopyConstructorFromEntity(JavaSourceWriter writer) {
        if (forDtoClasses) {
            if (classDef.hasTag()) {
                writer.emptyLine().writeBlockBeginln(PRIVATE_BUILDER + config.getIdClass().getName() + " id, java.lang.Integer etag)");
            } else {
                writer.emptyLine().writeBlockBeginln(PRIVATE_BUILDER + config.getIdClass() + " id)");
            }
            writeAssignmentIdAndEtag(writer);
            writer.writeBlockEnd();
        }
    }

    private void writeAssignmentIdAndEtag(JavaSourceWriter writer) {
        if (classDef.hasTag()) {
            writer.emptyLine()
                    .writeLine("this.id = id;")
                    .writeLine("this.etag = \"\" + etag;");
        } else {
            writer.emptyLine()
                    .writeLine("this.id = existing.getId();");
        }
    }

    private void writeBuilderSetters(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> {
            if (!fieldDef.isWriteOnce()) {
                writer.writeBlockBeginln("public Builder set" + capitalize(fieldDef.getName()) + "(" + fieldDef.getType()
                        + " " + fieldDef.getName() + ")");
                writer.assign(ASSIGNEE_PREFIX_THIS, fieldDef.getName(), "", fieldDef.getName());
                if (isForEntity()) {
                    writer.assign(ASSIGNEE_PREFIX_THIS, IS_MODIFIED, "", "true");
                }
                writer.writeLine(RETURN_THIS);
                writer.writeBlockEnd();
                fieldDef.fetchCollectionDef()
                        .ifPresent(collectionDef -> writeBuilderAddToCollection(fieldDef, collectionDef, writer));
            }
        });
    }

    private void writeBuilderIfNotNullSetters(JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> {
            if (!fieldDef.isWriteOnce()) {
                writer.writeBlockBeginln("public Builder set" + capitalize(fieldDef.getName()) + "IfNotNull("
                        + fieldDef.getType() + " " + fieldDef.getName()
                        + ")");
                writer.writeBlockBeginln("if (" + fieldDef.getName() + " != null)");
                writer.assign(ASSIGNEE_PREFIX_THIS, fieldDef.getName(), "", fieldDef.getName());
                if (isForEntity()) {
                    writer.assign(ASSIGNEE_PREFIX_THIS, IS_MODIFIED, "", "true");
                }
                writer.writeBlockEnd();
                writer.writeLine(RETURN_THIS);
                writer.writeBlockEnd();
            }
        });
    }

    private void writeBuilderBuildMethod(JavaSourceWriter writer) {
        writer.writeBlockBeginln("public " + getClassName() + " build()")
                .writeLine("return new " + getClassName() + "(this);")
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
                .writeLine(ASSIGNEE_PREFIX_THIS + fieldDef.getName() + " = new " + collectionDef.getCollectionImplementationType() + "<>();")
                .writeBlockEnd()
                .writeLine(ASSIGNEE_PREFIX_THIS + fieldDef.getName() + ".add(" + fieldDef.getSingular() + ");")
                .writeLine(RETURN_THIS)
                .writeBlockEnd();
    }

    private void writeAssignmentsInBuilderConstructorUsingFields(String assigneePrefix, String assignedValuePrefix, JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> builderAssignFromFields(assigneePrefix, assignedValuePrefix, fieldDef, writer));
    }

    private void writeAssignmentsInBuilderConstructorUsingGetters(String assigneePrefix, String assignedValuePrefix, JavaSourceWriter writer) {
        classDef.getFieldDefs().forEach(fieldDef -> builderAssignFromGetters(assigneePrefix, assignedValuePrefix, fieldDef, writer));
    }

    private void builderAssignFromFields(String assigneePrefix, String assignedFieldPrefix, EntityFieldDef entityFieldDef, JavaSourceWriter writer) {
        writer.writeLine(assigneePrefix + entityFieldDef.getName() + " = " + assignedFieldPrefix + entityFieldDef.getName() + ";");
    }

    private void builderAssignFromGetters(String assigneePrefix, String assignedFieldPrefix, EntityFieldDef entityFieldDef, JavaSourceWriter writer) {
        writer.writeLine(assigneePrefix + entityFieldDef.getName() + " = " + assignedFieldPrefix + "get" + capitalize(entityFieldDef.getName()) + "();");
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
                .writeBlockEnd()
                .emptyLine();
        if (forDtoClasses) {
            String entityClassName = config.getTargetPackage() + "." + classDef.getName();
            String entityBuilderClassName = config.getTargetPackage() + "." + classDef.getName() + ".Builder";
            writer.writeBlockBeginln("public Builder fromEntity(" + entityClassName + " existing)")
                    .writeLine("return new Builder(existing);")
                    .writeBlockEnd()
                    .emptyLine();

            writer.writeBlockBeginln("public " + entityBuilderClassName + " copyToBuilder(" + entityClassName + " existing)")
                    .writeLine("return existing.modify()");

            classDef.getFieldDefs().forEach(fieldDef -> {
                writer.writeLine(".set" + capitalize(fieldDef.getName()) + "(this.get" + capitalize(fieldDef.getName()) + "())");
            });
            writer
                    .writeLine(";")
                    .writeBlockEnd()
                    .emptyLine();
            writer.writeBlockBeginln("public " + entityBuilderClassName + " copyToBuilderNotNull(" + entityClassName + " existing)")
                    .writeLine(entityBuilderClassName + " builder = existing.modify();");
            classDef.getFieldDefs().forEach(fieldDef ->
                writer.writeLine("java.util.Optional.ofNullable(get"
                        + capitalize(fieldDef.getName()) + "()).ifPresent(newValue -> builder.set"
                        + capitalize(fieldDef.getName()) + "(newValue));"));
            writer.writeLine("return builder;")
                    .writeBlockEnd()
                    .emptyLine();
        }
    }

    private void writeCreateFromPersistableEntity(JavaSourceWriter writer) {
        if (forDtoClasses) {
            if (classDef.hasTag()) {
                writer.writeBlockBeginln("public static Builder from(PersistableEntityWithTag existing)")
                        .writeLine("return new Builder(existing.getId(), existing.getEtag());")
                        .writeBlockEnd()
                        .emptyLine();

            } else {
                writer.writeBlockBeginln("public static Builder from(PersistableEntity identifiable existing)")
                        .writeLine("return new Builder(existing.getId());")
                        .writeBlockEnd()
                        .emptyLine();
            }
        }
    }

    private void writeFieldDeclaration(EntityFieldDef entityFieldDef, boolean isFinal, JavaSourceWriter writer) {
        String prefix = isFinal ? "private final " : "private ";
        writer.writeLine(prefix + entityFieldDef.getType() + " " + entityFieldDef.getName() + ";");
    }
}
