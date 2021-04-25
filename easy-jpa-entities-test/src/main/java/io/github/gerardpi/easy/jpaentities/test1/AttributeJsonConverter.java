package io.github.gerardpi.easy.jpaentities.test1;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;

import static java.util.Objects.nonNull;

public abstract class AttributeJsonConverter<T> implements AttributeConverter<T, String> {
    private static final Logger LOG = LoggerFactory.getLogger(AttributeJsonConverter.class);

    private final ObjectMapperHolder objectMapperHolder;
    private final Class<T> targetClass;
    private final CollectionType collectionType;
    private final MapType mapType;

    public AttributeJsonConverter(Class<T> targetClass) {
        this(targetClass, null, null);
    }

    public AttributeJsonConverter(CollectionType collectionType) {
        this(null, collectionType, null);
    }

    public AttributeJsonConverter(MapType mapType) {
        this(null, null, mapType);
    }

    public AttributeJsonConverter(Class<T> targetClass, CollectionType collectionType, MapType mapType) {
        this.objectMapperHolder = ObjectMapperHolder.INSTANCE;
        this.targetClass = targetClass;
        this.collectionType = collectionType;
        this.mapType = mapType;
    }

    private String toJson(T something) {
        return objectMapperHolder.toJson(something);
    }

    private T fromJson(String json) {
        if (nonNull(targetClass)) {
            return ObjectMapperHolder.INSTANCE.fromJson(json, targetClass);
        } else if (nonNull(collectionType)) {
            return ObjectMapperHolder.INSTANCE.fromJson(json, collectionType);
        } else {
            return ObjectMapperHolder.INSTANCE.fromJson(json, mapType);
        }
    }

    @Override
    public final String convertToDatabaseColumn(T attribute) {
        return toJson(attribute);
    }

    @Override
    public final T convertToEntityAttribute(String json) {
        return fromJson(json);
    }
}
