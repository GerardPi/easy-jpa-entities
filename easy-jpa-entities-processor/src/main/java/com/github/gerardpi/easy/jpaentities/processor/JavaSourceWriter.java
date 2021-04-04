package com.github.gerardpi.easy.jpaentities.processor;

import com.github.gerardpi.easy.jpaentities.processor.entitydefs.CollectionDef;
import com.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;
import com.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityFieldDef;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
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
            writer.emptyLine();
            assign("this.", fieldDef.getName(), "", fieldDef.getName());
            writer.line("return this;");
            writeBlockEnd();
            fieldDef.getCollectionDef()
                    .ifPresent(collectionDef -> {
                        writeBlockBeginln("public Builder add" + capitalize(fieldDef.getSingular()) + ("(" + collectionDef.getCollectedType() + " " + fieldDef.getSingular() + ")"));
                        writeBlockBeginln("if (this." + fieldDef.getName() + " == null)");
                        writer.line("this." + fieldDef.getName() + " = new " + collectionDef.getCollectionImplementationType() + "<>();");
                        writeBlockEnd();
                        writer.line("this." + fieldDef.getName() + ".add(" + fieldDef.getSingular() + ");");
                        writer.line("return this;");
                        writeBlockEnd();
            });
        });
            return this;
    }

    JavaSourceWriter emptyLine() {
        writer.emptyLine();
        return this;
    }

    JavaSourceWriter writeFieldDeclaration(String fieldType, String fieldName, boolean isFinal, List<String> annotations) {
        writer.line("public static final String PROPNAME_" + fieldName.toUpperCase() + " = " + quoted(fieldName) + ";");
        annotations.forEach(annotation -> {
            writer.line("@" + annotation);
        });
        String prefix = isFinal ? "private final " : "private ";
        writer.line(prefix + fieldType + " " + fieldName + ";");
        return this;
    }

    JavaSourceWriter writeEntityFieldDeclarations(List<EntityFieldDef> fieldDefs) {
        fieldDefs.forEach(fieldDef -> writeFieldDeclaration(fieldDef.getType(), fieldDef.getName(), true, fieldDef.getAnnotations()));
        return this;
    }

    JavaSourceWriter writeBuilderFieldDeclarations(List<EntityFieldDef> fieldDefs) {
        fieldDefs.forEach(fieldDef -> writeFieldDeclaration(fieldDef.getType(), fieldDef.getName(), false, Collections.emptyList()));
        return this;
    }


    JavaSourceWriter writeCreateAndModifyWithBuilderMethods() {
        writeBlockBeginln("public static Builder create(java.util.UUID id)");
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

    JavaSourceWriter writeFieldGetters(List<EntityFieldDef> fieldDefs) {
        fieldDefs.forEach(fieldDef -> {
            writeMethodSignature(fieldDef.getType(), "get" + capitalize(fieldDef.getName()));
            writer.line("return " + fieldDef.getName() + ";");
            writeBlockEnd();
        });
        return this;
    }

    JavaSourceWriter assign(String assigneeFieldName, String valueFieldName) {
        return assign("", assigneeFieldName, "", valueFieldName);
    }

    JavaSourceWriter assign(String assigneePrefix, String assigneeFieldName, String assignedFieldPrefix, String valueFieldName) {
        writer.line(assigneePrefix + assigneeFieldName + " = " + assignedFieldPrefix + valueFieldName + ";");
        return this;
    }

    JavaSourceWriter writeAssignments(List<EntityFieldDef> fieldDefs) {
        return writeAssignments(fieldDefs, THIS_PREFIX);
    }

    JavaSourceWriter writeAssignments(List<EntityFieldDef> fieldDefs, String assigneePrefix, String assignedValuePrefix) {
        fieldDefs.forEach(fieldDef -> assign(assigneePrefix,  assignedValuePrefix, fieldDef));
        return this;
    }

    String immutable(String fieldPrefix, EntityFieldDef fieldDef) {
        CollectionDef collectionDef = fieldDef.getCollectionDef().orElseThrow(() -> new IllegalArgumentException("No " + CollectionDef.class + " could be found"));
        if (collectionDef.isSortedSet()) {
            return ImmutableSortedSet.class.getName() + ".copyOf(" + fieldPrefix + fieldDef.getName() + ")";
        } else if (collectionDef.isList()) {
            return ImmutableList.class.getName() + ".copyOf(" + fieldPrefix + fieldDef.getName() + ")";
        } else if (collectionDef.isSet()) {
            return ImmutableSet.class.getName() + ".copyOf(" + fieldPrefix + fieldDef.getName() + ")";
        }
        throw new IllegalArgumentException("No idea what to do with a collection of type '" + fieldDef.getCollectionDef().get().getCollectionType() + "'.");
    }

    JavaSourceWriter assign(String assigneePrefix, String assignedFieldPrefix, EntityFieldDef entityFieldDef) {
        if (entityFieldDef.getCollectionDef().isPresent()) {
            writer.line(assigneePrefix + entityFieldDef.getName() + " = " + immutable(assignedFieldPrefix, entityFieldDef) + ";");
        } else {
            writer.line(assigneePrefix + entityFieldDef.getName() + " = " + assignedFieldPrefix + entityFieldDef.getName() + ";");
        }
        return this;
    }

    JavaSourceWriter writeAssignments(List<EntityFieldDef> fieldDefs, String assigneePrefix) {
        return writeAssignments(fieldDefs, assigneePrefix, "");
    }

    JavaSourceWriter writeAssignmentsToNull(List<EntityFieldDef> fieldDefs) {
        fieldDefs.forEach(fieldDef -> assign(THIS_PREFIX + fieldDef.getName(), "null"));
        return this;
    }

    private String methodParameterDeclarations(List<EntityFieldDef> fieldDefs) {
        return fieldDefs.stream()
                .map(fieldDef -> fieldDef.getType() + " " + fieldDef.getName())
                .collect(Collectors.joining(", "));
    }

    JavaSourceWriter writeConstructor(EntityClassDef classDef) {
        writeBlockBeginln("private " + classDef.getName() + "(" + methodParameterDeclarations(classDef.getFieldDefs()) + ")");
        writeAssignments(classDef.getFieldDefs());
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
        writer.line(classDef.isRewritable() ? "super(builder.id, builder.optLockVersion);" : "super(builder.id);");
        writeAssignments(classDef.getFieldDefs(), "this.", "builder.");
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
