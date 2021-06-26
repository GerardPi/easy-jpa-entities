package io.github.gerardpi.easy.jpaentities.test1.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * https://www.baeldung.com/jackson-serialize-dates
 */
public class JsonObjectMapperFactory {
    private static final Logger LOG = LoggerFactory.getLogger(JsonObjectMapperFactory.class);

    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(createJavaTimeModule());
        LOG.info("Using modules: ", objectMapper.getRegisteredModuleIds());

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        return objectMapper;
    }

    private static Module createJavaTimeModule() {
        return new JavaTimeModule();
    }

    public static ObjectMapper createPrettyPrintingObjectMapper() {
        return createObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }
}
