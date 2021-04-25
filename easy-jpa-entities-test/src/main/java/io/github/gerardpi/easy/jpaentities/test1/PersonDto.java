package io.github.gerardpi.easy.jpaentities.test1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class PersonDto {
    private String nameFirst;
    private String nameLast;
    private String eTag;

    @JsonCreator
    public PersonDto(@JsonProperty("nameFirst") String nameFirst, @JsonProperty("nameLast") String nameLast, @JsonProperty("eTag") String eTag) {
        this.nameFirst = nameFirst;
        this.nameLast = nameLast;
    }

    public String getNameFirst() {
        return nameFirst;
    }

    public String getNameLast() {
        return nameLast;
    }

    public String geteTag() {
        return eTag;
    }

    Person.Builder copyValues(Person.Builder personBuilder) {
        return personBuilder.setNameFirst(nameFirst).setNameLast(nameLast);
    }

    Person toPerson(UUID id) {
        return copyValues(Person.create(id)).build();
    }
    Person toPerson(Person person) {
        return copyValues(person.modify()).build();
    }

    static PersonDto fromPerson(Person person) {
        return new PersonDto(person.getNameFirst(), person.getNameLast(), "" + person.getOptLockVersion());
    }
}
