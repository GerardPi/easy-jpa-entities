package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.PersistableDefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MappedSuperclassGenerator {
    public static final String CLASSNAME_PERSISTABLE = "Persistable";
    public static final String CLASSNAME_OPT_LOCKABLE_PERSISTABLE = "OptLockablePersistable";
    private final String packageName;

    MappedSuperclassGenerator(String packageName) {
        this.packageName = packageName;
    }

    void writePersistable(LineWriter writer, PersistableDefs persistableDefs) {
        write(CLASSNAME_PERSISTABLE + "-java.txt", writer, persistableDefs);
    }

    void writeOptLockablePersistable(LineWriter writer, PersistableDefs persistableDefs) {
        write(CLASSNAME_OPT_LOCKABLE_PERSISTABLE + "-java.txt", writer, persistableDefs);
    }

    private void write(String resourceName, LineWriter writer, PersistableDefs persistableDefs) {
        writer.line("package " + packageName + ";");
        writer.line("// Generated date/time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(MappedSuperclassGenerator.class.getResourceAsStream(resourceName), StandardCharsets.UTF_8))) {
            reader.lines()
                    .map(line -> replace(line, persistableDefs))
                    .forEach(writer::line);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String replace(String line, PersistableDefs persistableDefs) {
        String result = line;
        for (Map.Entry<String, String> replacement : persistableDefs.getTagReplacementMap().entrySet()) {
            result = result.replace(replacement.getKey(), replacement.getValue());
        }
        return result;
    }
}
