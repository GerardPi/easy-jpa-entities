package io.github.gerardpi.easy.jpaentities.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.CollectionDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityFieldDef;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class JavaSourceWriter implements AutoCloseable {
    public static final String BLOCK_BEGIN = " {";
    public static final String BLOCK_END = "}";
    public static final String THIS_PREFIX = "this.";
    private final LineWriter writer;


    JavaSourceWriter(LineWriter writer) {
        this.writer = writer;
    }

    static String capitalize(String part) {
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

    static String unCapitalize(String part) {
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

    @Override
    public void close() throws IOException {
        writer.close();
    }

    JavaSourceWriter writeLine(String line) {
        writer.line(line);
        return this;
    }

    JavaSourceWriter emptyLine() {
        writer.emptyLine();
        return this;
    }

    JavaSourceWriter assignNull(EntityFieldDef fieldDef) {
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


    String immutable(String fieldPrefix, EntityFieldDef fieldDef, boolean assignedValueMustBeImmutable) {
        CollectionDef collectionDef = fieldDef.fetchCollectionDef().orElseThrow(() -> new IllegalArgumentException("No " + CollectionDef.class + " could be found"));
        if (collectionDef.isSortedSet()) {
            return assignedValueMustBeImmutable
                    ? ImmutableSortedSet.class.getName() + ".copyOf(" + fieldPrefix + fieldDef.getName() + ")"
                    : fieldPrefix + fieldDef.getName();
        } else if (collectionDef.isList()) {
            return assignedValueMustBeImmutable
                    ? ImmutableList.class.getName() + ".copyOf(" + fieldPrefix + fieldDef.getName() + ")"
                    : fieldPrefix + fieldDef.getName();
        } else if (collectionDef.isSet()) {
            return assignedValueMustBeImmutable
                    ? ImmutableSet.class.getName() + ".copyOf(" + fieldPrefix + fieldDef.getName() + ")"
                    : fieldPrefix + fieldDef.getName();
        }
        throw new IllegalArgumentException("No idea what to do with a collection of type '" + fieldDef.fetchCollectionDef().get().getCollectionType() + "'.");
    }

    JavaSourceWriter writeAssignmentsToNull(List<EntityFieldDef> fieldDefs) {
        fieldDefs.forEach(fieldDef -> assignNull(fieldDef));
        return this;
    }

    JavaSourceWriter assign(String assigneePrefix, String assignedFieldPrefix, EntityFieldDef entityFieldDef, boolean assignedValueMustBeImmutable) {
        if (entityFieldDef.fetchCollectionDef().isPresent()) {
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
    String quoted(String value) {
        return "\"" + value + "\"";
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


    public JavaSourceWriter incIndentation() {
        writer.incIndentation();
        return this;
    }

    public JavaSourceWriter decIndentation() {
        writer.decIndentation();
        return this;
    }
}
