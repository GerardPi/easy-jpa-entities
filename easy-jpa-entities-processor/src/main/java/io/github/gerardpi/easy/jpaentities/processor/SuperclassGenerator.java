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

public class SuperclassGenerator {
   private final EasyJpaEntitiesConfig config;

    SuperclassGenerator(EasyJpaEntitiesConfig config) {
        this.config = config;
    }

    public void write(String superClassName, LineWriter writer) {
        String resourceName = superClassName + "-java.txt";
        writer.line("package " + config.getTargetPackage() + ";");
        if (config.isIncludeCommentWithTimestamp()) {
            writer
                    .line("// Generated")
                    .line("//         date/time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                    .line("//         details: https://github.com/GerardPi/easy-jpa-entities");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(SuperclassGenerator.class.getResourceAsStream(resourceName), StandardCharsets.UTF_8))) {
            reader.lines()
                    .map(this::replace)
                    .forEach(writer::line);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    private String replace(String line) {
        String result = line;
        for (Map.Entry<String, String> replacement : config.getTagReplacementMap().entrySet()) {
            result = result.replace(replacement.getKey(), replacement.getValue());
        }
        return result;
    }
}
