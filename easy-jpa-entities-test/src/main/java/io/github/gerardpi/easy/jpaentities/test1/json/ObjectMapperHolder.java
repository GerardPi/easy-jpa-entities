package io.github.gerardpi.easy.jpaentities.test1.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;


public enum ObjectMapperHolder {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(ObjectMapperHolder.class);

    private ObjectMapper objectMapper;

    ObjectMapperHolder() {
        LoggerFactory.getLogger(ObjectMapperHolder.class).info("Setting an initial objectMapper");
        this.objectMapper = JsonObjectMapperFactory.createObjectMapper();
    }

    public static ObjectMapperHolder getIntance() {
        return INSTANCE;
    }

    public String toJson(List<?> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> T fromJson(String json, Class<T> targetClass) {
        try {
            return objectMapper.readValue(json, targetClass);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> T fromJson(String json, CollectionType collectionType) {
        try {
            return objectMapper.readValue(json, collectionType);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> T fromJson(String json, MapType mapType) {
        try {
            return objectMapper.readValue(json, mapType);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        if (Objects.nonNull(this.objectMapper)) {
            LOG.info("Setting a new objectMapper");
        }
        this.objectMapper = objectMapper;
    }
}

