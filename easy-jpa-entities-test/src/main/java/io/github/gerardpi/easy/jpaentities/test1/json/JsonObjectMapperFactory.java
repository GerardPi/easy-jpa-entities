package io.github.gerardpi.easy.jpaentities.test1.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

/**
 * https://www.baeldung.com/jackson-serialize-dates
 */
public class JsonObjectMapperFactory {
    private JsonObjectMapperFactory() {
        // No instantiation allowed
    }

    public static ObjectMapper createObjectMapper() {
        return create()
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private static ObjectMapper create() {
        final ObjectMapper objectMapper = new ObjectMapper();
        //
        // Required to prevent LocalDate being written als array of integers.
        objectMapper.registerModules(new JavaTimeModule(), new ParameterNamesModule());

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        return objectMapper;

    }

    public static ObjectMapper createPrettyPrintingObjectMapper() {
        return create()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }
}
