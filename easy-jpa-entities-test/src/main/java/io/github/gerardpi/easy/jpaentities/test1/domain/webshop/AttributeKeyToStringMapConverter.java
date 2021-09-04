package io.github.gerardpi.easy.jpaentities.test1.domain.webshop;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.gerardpi.easy.jpaentities.test1.persistence.AttributeJsonConverter;

import javax.persistence.Converter;
import java.util.SortedMap;

@Converter
public class AttributeKeyToStringMapConverter extends AttributeJsonConverter<SortedMap<ItemAttributeKey, String>> {
    public AttributeKeyToStringMapConverter() {
        super(TypeFactory.defaultInstance()
                .constructMapType(SortedMap.class, ItemAttributeKey.class, String.class));
    }
}
