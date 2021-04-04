package com.github.gerardpi.easy.jpaentities.test1;

import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.persistence.Converter;
import java.util.SortedSet;

@Converter
public class SortedStringSetConverter extends AttributeJsonConverter<SortedSet<String>> {
    public SortedStringSetConverter() {
        super(TypeFactory.defaultInstance().constructCollectionType(SortedSet.class, String.class));
    }
}
