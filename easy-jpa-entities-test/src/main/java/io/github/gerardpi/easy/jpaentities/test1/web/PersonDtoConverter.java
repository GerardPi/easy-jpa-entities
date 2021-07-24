package io.github.gerardpi.easy.jpaentities.test1.web;

import io.github.gerardpi.easy.jpaentities.test1.domain.Person;
import org.springframework.core.convert.converter.Converter;

public class PersonDtoConverter implements Converter<Person, PersonDto> {
    @Override
    public PersonDto convert(Person person) {
        return PersonDto.create()
                .setName(person.getName())
                .setDateOfBirth(person.getDateOfBirth())
                .build();
    }

}
