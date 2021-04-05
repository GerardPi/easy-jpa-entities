package com.github.gerardpi.easy.jpaentities.processor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.stream.Stream;

class LineWriter implements AutoCloseable {
    private static final Indentation indentation = new Indentation(5, 2);
    private final Writer writer;
    private final String lineSeparator = System.lineSeparator();

    LineWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    LineWriter decIndentation() {
        indentation.dec();
        return this;
    }

    LineWriter incIndentation() {
        indentation.inc();
        return this;
    }

    LineWriter write(String string) {
        try {
            writer.write(string);
            return this;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    LineWriter line(String string) {
        write(indentation.get() + string + lineSeparator);
        return this;
    }

    LineWriter endln() {
        write(lineSeparator);
        return this;
    }

    LineWriter lines(Stream<String> lines) {
        lines.forEach(this::line);
        return this;
    }

    LineWriter emptyLine() {
        return line("");
    }
}
