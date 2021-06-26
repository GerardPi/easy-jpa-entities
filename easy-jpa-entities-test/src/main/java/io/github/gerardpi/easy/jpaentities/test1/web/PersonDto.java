package io.github.gerardpi.easy.jpaentities.test1.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.gerardpi.easy.jpaentities.test1.domain.Person;
import io.github.gerardpi.easy.jpaentities.test1.domain.PersonName;

import java.util.UUID;

public class PersonDto {
    private final PersonName name;
    private final String eTag;

    @JsonCreator
    public PersonDto(@JsonProperty("name") PersonName name, @JsonProperty("eTag") String eTag) {
        this.name = name;
        this.eTag = eTag;
    }

    public PersonName getName() {
        return name;
    }

    public String geteTag() {
        return eTag;
    }

    Person.Builder copyValues(Person.Builder personBuilder) {
        return personBuilder.setName(PersonName.create().setFirst(name.getFirst()).setLast(name.getLast()).build());
    }

    Person toPerson(UUID id) {
        return copyValues(Person.create(id)).build();
    }
    Person toPerson(Person person) {
        return copyValues(person.modify()).build();
    }

    static PersonDto fromPerson(Person person) {
        return new PersonDto(person.getName(), "" + person.getOptLockVersion());
    }
}
