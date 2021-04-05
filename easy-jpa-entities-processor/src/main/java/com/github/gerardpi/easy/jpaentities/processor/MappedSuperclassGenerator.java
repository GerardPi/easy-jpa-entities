package com.github.gerardpi.easy.jpaentities.processor;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class MappedSuperclassGenerator {
    public static final String CLASSNAME_PERSISTABLE = "Persistable";
    public static final String CLASSNAME_REWRITABLE_PERSISTABLE = "RewritablePersistable";
    private final String packageName;

    MappedSuperclassGenerator(String packageName) {
        this.packageName = packageName;
    }

    void writePersistable(LineWriter writer) {
        write(CLASSNAME_PERSISTABLE + "-java.txt", writer);
    }

    void writeRewritablePersistable(LineWriter writer) {
        write(CLASSNAME_REWRITABLE_PERSISTABLE + "-java.txt", writer);
    }

    private void write(String resourceName, LineWriter writer) {
        writer.line("package " + packageName + ";");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(MappedSuperclassGenerator.class.getResourceAsStream(resourceName), StandardCharsets.UTF_8))) {
            writer.lines(reader.lines());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
