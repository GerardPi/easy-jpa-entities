package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.CollectionDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityFieldDef;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class JavaSourceWriter implements AutoCloseable {
    public static final String BLOCK_BEGIN = " {";
    public static final String BLOCK_END = "}";
    public static final String THIS_PREFIX = "this.";
    private final LineWriter writer;


    JavaSourceWriter(LineWriter writer) {
        this.writer = writer;
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    JavaSourceWriter writeClassDeclaration(EntityClassDef classDef) {
        String extendsPart = classDef.getExtendsFromClass() == null ? "" : " extends " + classDef.getExtendsFromClass();
        writeBlockBeginln("class " + classDef.getName() + extendsPart);
        return this;
    }

    JavaSourceWriter writePackageLine(String packageName) {
        writer.line("package " + packageName + ";");
        return this;
    }

    JavaSourceWriter writeLine(String line) {
        writer.line(line);
        return this;
    }

    JavaSourceWriter writeBuilderSetters(EntityClassDef classDef) {
        classDef.getFieldDefs().forEach(fieldDef -> {
            writeBlockBeginln("public Builder set" + capitalize(fieldDef.getName()) + "(" + fieldDef.getType() + " " + fieldDef.getName() + ")");
            assign("this.", fieldDef.getName(), "", fieldDef.getName());
            writer.line("return this;");
            writeBlockEnd();
            fieldDef.getCollectionDef()
                    .ifPresent(collectionDef -> writeBuilderAddToCollection(fieldDef, collectionDef));
        });
        return this;
    }

    void writeBuilderAddToCollection(EntityFieldDef fieldDef, CollectionDef collectionDef) {
        writeLine("/**");
        writeLine(" * CAUTION: If the entity used to create the builder already contained this collection");
        writeLine(" * then that collection probably is immutable.");
        writeLine(" * Before using this add... method, first replace it with a mutable copy using the setter.");
        writeLine(" * and only then use this add... method.");
        writeLine(" * If the collection contains nested objects, you probably want to create some algorithm");
        writeLine(" * specifically to make it possible to manipulate it and then use the setter to put it into the builder.");
        writeLine(" */");
        writeBlockBeginln("public Builder add" + capitalize(fieldDef.getSingular()) + ("(" + collectionDef.getCollectedType() + " " + fieldDef.getSingular() + ")"));
        writeBlockBeginln("if (this." + fieldDef.getName() + " == null)");
        writer.line("this." + fieldDef.getName() + " = new " + collectionDef.getCollectionImplementationType() + "<>();");
        writeBlockEnd();
        writer.line("this." + fieldDef.getName() + ".add(" + fieldDef.getSingular() + ");");
        writer.line("return this;");
        writeBlockEnd();
    }

    JavaSourceWriter emptyLine() {
        writer.emptyLine();
        return this;
    }

    JavaSourceWriter writeFieldDeclaration(EntityFieldDef entityFieldDef, boolean isFinal, List<String> otherEntityClassNames) {
        writer.line("public static final String PROPNAME_" + entityFieldDef.getName().toUpperCase() + " = " + quoted(entityFieldDef.getName()) + ";");
        entityFieldDef.getAnnotations().forEach(annotation -> writer.line("@" + annotation));
        return writeFieldDeclarationWithoutPropName(entityFieldDef, isFinal, otherEntityClassNames, false);
    }

    JavaSourceWriter writeFieldDeclarationWithoutPropName(EntityFieldDef entityFieldDef, boolean isFinal, List<String> otherEntityClassNames, boolean forBuilder) {
        if (forBuilder) {
            Optional<CollectionDef> optCollectionDef = entityFieldDef.getCollectionDef();
            if (optCollectionDef.isPresent()) {
                CollectionDef collectionDef = optCollectionDef.get();
                if (otherEntityClassNames.contains(collectionDef.getCollectedType())) {
                    String type = collectionDef.getCollectionType() + "<" + collectionDef.getCollectedType() + "." + "Builder>";
                    String name = unCapitalize(collectionDef.getCollectedType()) + "Builders";
                    writer.line("private final " + type + " " + name + ";");
                }
            } else {
                String prefix = isFinal ? "private final " : "private ";
                writer.line(prefix + entityFieldDef.getType() + " " + entityFieldDef.getName() + ";");
            }
        } else {
            String prefix = isFinal ? "private final " : "private ";
            writer.line(prefix + entityFieldDef.getType() + " " + entityFieldDef.getName() + ";");
        }
        return this;
    }

    JavaSourceWriter writeEntityFieldDeclarations(List<EntityFieldDef> fieldDefs) {
        fieldDefs.forEach(fieldDef -> writeFieldDeclaration(fieldDef, false, Collections.emptyList()));
        return this;
    }

    JavaSourceWriter writeBuilderFieldDeclarations(List<EntityFieldDef> fieldDefs, List<String> otherEntityClassNames) {
        fieldDefs.forEach(fieldDef ->
                writeFieldDeclarationWithoutPropName(fieldDef, false, otherEntityClassNames, true)
        );
        return this;
    }

    JavaSourceWriter writeCreateAndModifyWithBuilderMethods(Class<?> idClass) {
        writeBlockBeginln("public static Builder create(" + idClass.getName() + " id)");
        writer.line("return new Builder(id);");
        writeBlockEnd();
        writer.emptyLine();
        writeBlockBeginln("public Builder modify()");
        writer.line("return new Builder(this);");
        writeBlockEnd();
        return this;
    }

    String capitalize(String part) {
        if (part != null) {
            if (part.length() > 1) {
                return Character.toString(part.charAt(0)).toUpperCase()
                        + part.substring(1);
            } else {
                return part.toUpperCase();
            }
        }
        return "";
    }

    String unCapitalize(String part) {
        if (part != null) {
            if (part.length() > 1) {
                return Character.toString(part.charAt(0)).toLowerCase()
                        + part.substring(1);
            } else {
                return part.toUpperCase();
            }
        }
        return "";
    }

    JavaSourceWriter writeFieldGetters(List<EntityFieldDef> fieldDefs) {
        fieldDefs.forEach(fieldDef -> {
            writeMethodSignature(fieldDef.getType(), "get" + capitalize(fieldDef.getName()));
            writer.line("return " + fieldDef.getName() + ";");
            writeBlockEnd();
        });
        return this;
    }

    JavaSourceWriter assign(EntityFieldDef fieldDef) {
        return assign(THIS_PREFIX, fieldDef.getName(), "", "null");
    }

    JavaSourceWriter assign(String assigneePrefix, String assigneeFieldName, String assignedFieldPrefix, String valueFieldName) {
        writer.line(assigneePrefix + assigneeFieldName + " = " + assignedFieldPrefix + valueFieldName + ";");
        return this;
    }


    JavaSourceWriter writeAssignmentsInConstructor(List<EntityFieldDef> fieldDefs, String assigneePrefix, String assignedValuePrefix) {
        fieldDefs.forEach(fieldDef -> assign(assigneePrefix, assignedValuePrefix, fieldDef, true));
        return this;
    }

    JavaSourceWriter writeAssignmentsInBuilderConstructor(
            List<EntityFieldDef> fieldDefs,
            String assigneePrefix,
            String assignedValuePrefix,
            List<String> otherClassNames) {
        fieldDefs.forEach(fieldDef -> builderAssign(assigneePrefix, assignedValuePrefix, fieldDef, false, otherClassNames));
        return this;
    }

    String immutable(String fieldPrefix, EntityFieldDef fieldDef, boolean assignedValueMustBeImmutable) {
        CollectionDef collectionDef = fieldDef.getCollectionDef().orElseThrow(() -> new IllegalArgumentException("No " + CollectionDef.class + " could be found"));
        if (collectionDef.isSortedSet()) {
            return assignedValueMustBeImmutable
                    ? ImmutableSortedSet.class.getName() + ".copyOf("+ fieldPrefix + fieldDef.getName() + ")"
                    : fieldPrefix + fieldDef.getName();
        } else if (collectionDef.isList()) {
            return assignedValueMustBeImmutable
                    ? ImmutableList.class.getName() + ".copyOf("+ fieldPrefix + fieldDef.getName() + ")"
                    : fieldPrefix + fieldDef.getName();
        } else if (collectionDef.isSet()) {
            return assignedValueMustBeImmutable
                    ? ImmutableSet.class.getName() + ".copyOf(" + fieldPrefix + fieldDef.getName() + ")"
                    : fieldPrefix + fieldDef.getName();
        }
        throw new IllegalArgumentException("No idea what to do with a collection of type '" + fieldDef.getCollectionDef().get().getCollectionType() + "'.");
    }
    JavaSourceWriter writeAssignmentsToNull(List<EntityFieldDef> fieldDefs) {
        fieldDefs.forEach(fieldDef -> assign(fieldDef));
        return this;
    }

    JavaSourceWriter builderAssign(String assigneePrefix, String assignedFieldPrefix, EntityFieldDef entityFieldDef, boolean assignedValueMustBeImmutable, List<String> otherClassNames) {
        if (entityFieldDef.getCollectionDef().isPresent()) {
            CollectionDef collectionDef = entityFieldDef.getCollectionDef().get();
            if (otherClassNames.contains(collectionDef.getCollectedType())) {
                writeCollectionToBuilders(assigneePrefix, assignedFieldPrefix, entityFieldDef, collectionDef);
            } else {
                writer.line(assigneePrefix + entityFieldDef.getName() + " = " + immutable(assignedFieldPrefix, entityFieldDef, assignedValueMustBeImmutable) + ";");
            }
        } else {
            writer.line(assigneePrefix + entityFieldDef.getName() + " = " + assignedFieldPrefix + entityFieldDef.getName() + ";");
        }
        return this;
    }

    JavaSourceWriter writeCollectionToBuilders(String assigneePrefix, String assignedFieldPrefix, EntityFieldDef entityFieldDef, CollectionDef collectionDef) {
        writer.line(assigneePrefix + unCapitalize(collectionDef.getCollectedType()) + "Builders = " + assignedFieldPrefix + entityFieldDef.getName() +
                        ".stream().map(e -> e.modify()).collect(java.util.stream.Collectors.toList());");
        return this;
    }

    JavaSourceWriter assign(String assigneePrefix, String assignedFieldPrefix, EntityFieldDef entityFieldDef, boolean assignedValueMustBeImmutable) {
        if (entityFieldDef.getCollectionDef().isPresent()) {
            writer.line(assigneePrefix + entityFieldDef.getName() + " = " + immutable(assignedFieldPrefix, entityFieldDef, assignedValueMustBeImmutable) + ";");
        } else {
            writer.line(assigneePrefix + entityFieldDef.getName() + " = " + assignedFieldPrefix + entityFieldDef.getName() + ";");
        }
        return this;
    }



    private String methodParameterDeclarations(List<EntityFieldDef> fieldDefs) {
        return fieldDefs.stream()
                .map(fieldDef -> fieldDef.getType() + " " + fieldDef.getName())
                .collect(Collectors.joining(", "));
    }

    JavaSourceWriter writeConstructor(EntityClassDef classDef) {
        writeBlockBeginln("private " + classDef.getName() + "(" + methodParameterDeclarations(classDef.getFieldDefs()) + ")");
        writeAssignmentsInConstructor(classDef.getFieldDefs(), THIS_PREFIX, "");
        writeBlockEnd();
        return this;
    }

    JavaSourceWriter writeDefaultConstructor(EntityClassDef classDef) {
        writeBlockBeginln(classDef.getName() + "()");
        writeAssignmentsToNull(classDef.getFieldDefs());
        writeBlockEnd();
        return this;
    }

    String quoted(String value) {
        return "\"" + value + "\"";
    }

    JavaSourceWriter writeToStringMethod(boolean isRewritable, List<EntityFieldDef> fieldDefs) {
        writer.line("@Override");
        writeBlockBeginln("public String toString()");
        writer.line("return " + quoted("class=") + " + this.getClass().getName()");
        writer.incIndentation();
        writer.line("+ " + quoted(";id=") + "+ this.getId()");

        if (isRewritable) {
            writer.line("+ " + quoted(";optLockVersion=") + " + this.getOptLockVersion()");
        }
        fieldDefs.forEach(fieldDef -> {
            writer.line("+ " + quoted(";" + fieldDef.getName() + "=") + " + this." + fieldDef.getName());
        });
        writer.line(";");
        writer.decIndentation();
        writeBlockEnd();
        return this;
    }

    JavaSourceWriter writeConstructorUsingBuilder(EntityClassDef classDef) {
        writeBlockBeginln(classDef.getName() + "(Builder builder)");
        writer.line(classDef.isOptLockable() ? "super(builder.id, builder.optLockVersion);" : "super(builder.id, builder.isNew);");
        writeAssignmentsInConstructor(classDef.getFieldDefs(), "this.", "builder.");
        writeBlockEnd();
        return this;
    }

    String blockBegin(String line) {
        return line + BLOCK_BEGIN;
    }

    JavaSourceWriter writeBlockBeginln(String line) {
        writer.line(blockBegin(line)).incIndentation();
        return this;
    }

    JavaSourceWriter writeMethodSignature(String type, String methodName) {
        return writeMethodSignature(type, methodName, Collections.emptyList());
    }

    JavaSourceWriter writeMethodSignature(String type, String methodName, List<EntityFieldDef> fieldDefs) {
        return writeBlockBeginln(type + " " + methodName + " (" + methodParameterDeclarations(fieldDefs) + ")");
    }

    JavaSourceWriter writeBlockEnd() {
        writer.decIndentation();
        writer.line(BLOCK_END);
        return this;
    }

    JavaSourceWriter writeBuilderBuildMethod(EntityClassDef classDef) {
        return writeBlockBeginln("public " + classDef.getName() + " build()")
                .writeLine("return new " + classDef.getName() + "(this);")
                .writeBlockEnd();
    }


    JavaSourceWriter writeConstructors(EntityClassDef classDef, boolean includeConstructorWithParameters) {
        writeDefaultConstructor(classDef);
        if (includeConstructorWithParameters) {
            if (classDef.isReadOnly()) {
                throw new IllegalStateException("A constructor with parameters must be include, but class is readOnly.");
            }
            writeConstructor(classDef);
        }
        if (!classDef.isReadOnly()) {
            writeConstructorUsingBuilder(classDef);
        }
        return this;
    }
}
