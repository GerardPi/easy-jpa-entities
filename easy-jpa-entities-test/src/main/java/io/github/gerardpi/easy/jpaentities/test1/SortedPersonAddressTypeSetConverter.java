package io.github.gerardpi.easy.jpaentities.test1;

import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.persistence.Converter;
import java.util.SortedSet;

@Converter
public class SortedPersonAddressTypeSetConverter extends AttributeJsonConverter<SortedSet<PersonAddressType>> {
    public SortedPersonAddressTypeSetConverter() {
        super(TypeFactory.defaultInstance().constructCollectionType(SortedSet.class, PersonAddressType.class));
    }
}
