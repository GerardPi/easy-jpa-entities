package com.github.gerardpi.easy.jpaentities.test1;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.persistence.Converter;
import java.util.SortedMap;

@Converter
public class LocalizedTextsMapConverter extends AttributeJsonConverter<SortedMap<Lang, SortedMap<ItemTextType, String>>> {
    public LocalizedTextsMapConverter() {
        super(createMapType(TypeFactory.defaultInstance()));
    }

    private static MapType createMapType(TypeFactory typeFactory) {
        JavaType langType = typeFactory.constructType(Lang.class);
        JavaType stringType = typeFactory.constructType(String.class);
        JavaType itemTextType = typeFactory.constructType(ItemTextType.class);

        return typeFactory.constructMapType(SortedMap.class, langType,
                typeFactory.constructMapType(SortedMap.class, itemTextType, stringType));
    }
}
