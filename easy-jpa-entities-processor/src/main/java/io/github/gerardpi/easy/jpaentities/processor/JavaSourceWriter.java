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
    public static final String COPY_OF = ".copyOf(";
    private final LineWriter writer;


    JavaSourceWriter(final LineWriter writer) {
        this.writer = writer;
    }

    static String capitalize(final String part) {
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

    static String immutable(final String fieldPrefix, final EntityFieldDef fieldDef, final boolean assignedValueMustBeImmutable) {
        final CollectionDef collectionDef = fieldDef.fetchCollectionDef().orElseThrow(() -> new IllegalArgumentException("No " + CollectionDef.class + " could be found"));
        if (collectionDef.isSortedSet()) {
            return assignedValueMustBeImmutable
                    ? ImmutableSortedSet.class.getName() + COPY_OF + fieldPrefix + fieldDef.getName() + ")"
                    : fieldPrefix + fieldDef.getName();
        } else if (collectionDef.isList()) {
            return assignedValueMustBeImmutable
                    ? ImmutableList.class.getName() + COPY_OF + fieldPrefix + fieldDef.getName() + ")"
                    : fieldPrefix + fieldDef.getName();
        } else if (collectionDef.isSet()) {
            return assignedValueMustBeImmutable
                    ? ImmutableSet.class.getName() + COPY_OF + fieldPrefix + fieldDef.getName() + ")"
                    : fieldPrefix + fieldDef.getName();
        }
        throw new IllegalArgumentException("No idea what to do with a collection of type '"
                + collectionDef.getCollectionType()
                + "'.");
    }

    static String quoted(final String value) {
        return "\"" + value + "\"";
    }

    static String blockBegin(final String line) {
        return line + BLOCK_BEGIN;
    }

    /**
     * Write lines, last line ends with a semicolon.
     */
    JavaSourceWriter writeLinesAsOneStatement(final List<String> lines) {
        if (!lines.isEmpty()) {
            if (lines.size() > 1) {
                lines.subList(0, lines.size() - 1).forEach(this::writeLine);
            }
            writeLine(lines.get(lines.size() - 1) + ";");
        }
        return this;
    }

    JavaSourceWriter writeLinesAsOneStatement(final String firstLine, final List<String> lines) {
        writeLine(firstLine)
                .incIndentation();
        writeLinesAsOneStatement(lines);
        decIndentation();
        return this;
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    JavaSourceWriter write(final String text) {
        writer.write(text);
        return this;
    }

    JavaSourceWriter writeLine(final String line) {
        writer.line(line);
        return this;
    }

    JavaSourceWriter emptyLine() {
        writer.emptyLine();
        return this;
    }

    JavaSourceWriter assignNull(final EntityFieldDef fieldDef) {
        return assign(THIS_PREFIX, fieldDef.getName(), "", "null");
    }

    JavaSourceWriter assign(final String assigneePrefix, final String assigneeFieldName, final String assignedFieldPrefix, final String valueFieldName) {
        writer.line(assigneePrefix + assigneeFieldName + " = " + assignedFieldPrefix + valueFieldName + ";");
        return this;
    }

    JavaSourceWriter writeAssignmentsToNull(final List<EntityFieldDef> fieldDefs) {
        fieldDefs.forEach(fieldDef -> {
            if (!fieldDef.isWriteOnce()) {
                assignNull(fieldDef);
            } else {
                assign(THIS_PREFIX, fieldDef.getName(), "", fieldDef.getName());
            }
        });
        return this;
    }

    JavaSourceWriter writeImport(final String className) {
        writeLine("import " + className + ";");
        return this;
    }

    JavaSourceWriter assign(final String assigneePrefix, final String assignedFieldPrefix, final EntityFieldDef entityFieldDef, final boolean assignedValueMustBeImmutable) {
        if (entityFieldDef.fetchCollectionDef().isPresent()) {
            writer.line(assigneePrefix + entityFieldDef.getName() + " = " + immutable(assignedFieldPrefix, entityFieldDef, assignedValueMustBeImmutable) + ";");
        } else {
            writer.line(assigneePrefix + entityFieldDef.getName() + " = " + assignedFieldPrefix + entityFieldDef.getName() + ";");
        }
        return this;
    }

    JavaSourceWriter writeBlockBeginln(final String line) {
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
