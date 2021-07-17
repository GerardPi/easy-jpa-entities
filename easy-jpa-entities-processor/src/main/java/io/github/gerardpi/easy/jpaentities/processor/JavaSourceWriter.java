package io.github.gerardpi.easy.jpaentities.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.CollectionDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityFieldDef;

import java.io.IOException;
import java.util.List;

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
        throw new IllegalArgumentException("No idea what to do with a collection of type '"
                + collectionDef.getCollectionType()
                + "'.");
    }

    JavaSourceWriter writeAssignmentsToNull(List<EntityFieldDef> fieldDefs) {
        fieldDefs.forEach(fieldDef -> {
            if (!fieldDef.isWriteOnce()) {
                assignNull(fieldDef);
            } else {
                assign(THIS_PREFIX, fieldDef.getName(), "", fieldDef.getName());
            }
        });
        return this;
    }

    JavaSourceWriter writeImport(String packageName, String className) {
        writeLine("import " + packageName + "." + className + ";");
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
