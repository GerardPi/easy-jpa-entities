package io.github.gerardpi.easy.jpaentities.test1.persistence;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import io.github.gerardpi.easy.jpaentities.test1.json.ObjectMapperHolder;

import javax.persistence.AttributeConverter;

import static java.util.Objects.nonNull;

public abstract class AttributeJsonConverter<T> implements AttributeConverter<T, String> {
    private final ObjectMapperHolder objectMapperHolder;
    private final Class<T> targetClass;
    private final CollectionType collectionType;
    private final MapType mapType;

    protected AttributeJsonConverter(final Class<T> targetClass) {
        this(targetClass, null, null);
    }

    protected AttributeJsonConverter(final CollectionType collectionType) {
        this(null, collectionType, null);
    }

    protected AttributeJsonConverter(final MapType mapType) {
        this(null, null, mapType);
    }

    protected AttributeJsonConverter(final Class<T> targetClass, final CollectionType collectionType, final MapType mapType) {
        this.objectMapperHolder = ObjectMapperHolder.INSTANCE;
        this.targetClass = targetClass;
        this.collectionType = collectionType;
        this.mapType = mapType;
    }

    private String toJson(final T something) {
        return objectMapperHolder.toJson(something);
    }

    private T fromJson(final String json) {
        if (nonNull(targetClass)) {
            return ObjectMapperHolder.INSTANCE.fromJson(json, targetClass);
        } else if (nonNull(collectionType)) {
            return ObjectMapperHolder.INSTANCE.fromJson(json, collectionType);
        } else {
            return ObjectMapperHolder.INSTANCE.fromJson(json, mapType);
        }
    }

    @Override
    public final String convertToDatabaseColumn(final T attribute) {
        return toJson(attribute);
    }

    @Override
    public final T convertToEntityAttribute(final String json) {
        return fromJson(json);
    }
}
