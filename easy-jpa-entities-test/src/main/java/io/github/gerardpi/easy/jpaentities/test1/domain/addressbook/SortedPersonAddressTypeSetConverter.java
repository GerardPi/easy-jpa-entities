package io.github.gerardpi.easy.jpaentities.test1.domain.addressbook;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.gerardpi.easy.jpaentities.test1.persistence.AttributeJsonConverter;

import javax.persistence.Converter;
import java.util.SortedSet;

@Converter
public class SortedPersonAddressTypeSetConverter extends AttributeJsonConverter<SortedSet<PersonAddressType>> {
    public SortedPersonAddressTypeSetConverter() {
        super(TypeFactory.defaultInstance().constructCollectionType(SortedSet.class, PersonAddressType.class));
    }
}
