package io.github.gerardpi.easy.jpaentities.test1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tngtech.jgiven.format.ArgumentFormatter;
import io.github.gerardpi.easy.jpaentities.test1.json.JsonObjectMapperFactory;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JgivenObjectPrettyFormatter implements ArgumentFormatter<Object> {
    private static final int INDENT_AMOUNT_SPACES = 10;
    private static final int SEPARATOR_SIZE = 70;
    private static final String INDENTATION = String.join("", Collections.nCopies(INDENT_AMOUNT_SPACES, " "));
    private static final String SEPARATOR = String.join("", Collections.nCopies(SEPARATOR_SIZE, "-"));

    private static String indent(final String lines) {
        return System.lineSeparator()
                + INDENTATION + SEPARATOR + System.lineSeparator()
                + Stream.of(lines.split(System.lineSeparator()))
                .map(line -> INDENTATION + line)
                .collect(Collectors.joining(System.lineSeparator()))
                + System.lineSeparator()
                + INDENTATION + SEPARATOR + System.lineSeparator();
    }

    @Override
    public String format(final Object argumentToFormat, final String... formatterArguments) {
        try {
            return indent(JsonObjectMapperFactory.createPrettyPrintingObjectMapper().writeValueAsString(argumentToFormat));
        } catch (final JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
