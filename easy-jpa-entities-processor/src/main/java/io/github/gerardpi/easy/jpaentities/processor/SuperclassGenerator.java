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

    SuperclassGenerator(final EasyJpaEntitiesConfig config) {
        this.config = config;
    }

    public void write(final String superClassName, final LineWriter writer) {
        final String resourceName = superClassName + "-java.txt";
        writer.line("package " + config.getCommonPackage() + ";");
        if (config.isIncludeCommentWithTimestamp()) {
            writer
                    .line("// Generated")
                    .line("//         date/time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                    .line("//         details: https://github.com/GerardPi/easy-jpa-entities");
        }
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(SuperclassGenerator.class.getResourceAsStream(resourceName), StandardCharsets.UTF_8))) {
            reader.lines()
                    .map(this::replace)
                    .forEach(writer::line);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    private String replace(final String line) {
        String result = line;
        for (final Map.Entry<String, String> replacement : config.getTagReplacementMap().entrySet()) {
            result = result.replace(replacement.getKey(), replacement.getValue());
        }
        return result;
    }
}
