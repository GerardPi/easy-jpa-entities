package io.github.gerardpi.easy.jpaentities.test1.domain.webshop;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.gerardpi.easy.jpaentities.test1.domain.Lang;
import io.github.gerardpi.easy.jpaentities.test1.persistence.AttributeJsonConverter;

import javax.persistence.Converter;
import java.util.SortedMap;

@Converter
public class LocalizedTextsMapConverter extends AttributeJsonConverter<SortedMap<Lang, SortedMap<ItemTextType, String>>> {
    public LocalizedTextsMapConverter() {
        super(createMapType(TypeFactory.defaultInstance()));
    }

    private static MapType createMapType(final TypeFactory typeFactory) {
        final JavaType langType = typeFactory.constructType(Lang.class);
        final JavaType stringType = typeFactory.constructType(String.class);
        final JavaType itemTextType = typeFactory.constructType(ItemTextType.class);

        return typeFactory.constructMapType(SortedMap.class, langType,
                typeFactory.constructMapType(SortedMap.class, itemTextType, stringType));
    }
}
