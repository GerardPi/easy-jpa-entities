package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;

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
    private final EasyJpaEntitiesConfig easyJpaEntitiesConfig;

    MappedSuperclassGenerator(EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        this.easyJpaEntitiesConfig = easyJpaEntitiesConfig;
    }

    void writePersistable(LineWriter writer) {
        write(CLASSNAME_PERSISTABLE + "-java.txt", writer);
    }

    void writeOptLockablePersistable(LineWriter writer) {
        write(CLASSNAME_OPT_LOCKABLE_PERSISTABLE + "-java.txt", writer);
    }

    private void write(String resourceName, LineWriter writer) {
        writer.line("package " + easyJpaEntitiesConfig.getTargetPackage() + ";")
                .line("// Generated")
                .line("//         date/time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .line("//         details: https://github.com/GerardPi/easy-jpa-entities");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(MappedSuperclassGenerator.class.getResourceAsStream(resourceName), StandardCharsets.UTF_8))) {
            reader.lines()
                    .map(this::replace)
                    .forEach(writer::line);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String replace(String line) {
        String result = line;
        for (Map.Entry<String, String> replacement : easyJpaEntitiesConfig.getTagReplacementMap().entrySet()) {
            result = result.replace(replacement.getKey(), replacement.getValue());
        }
        return result;
    }
}
