package io.github.gerardpi.easy.jpaentities.test1;

import io.github.gerardpi.easy.jpaentities.test1.domain.Person;
import io.github.gerardpi.easy.jpaentities.test1.domain.PersonName;
import io.github.gerardpi.easy.jpaentities.test1.domain.PersonRepository;

import java.time.LocalDate;

public class TestFunctions {
    public static Person storeAndReturnPerson(String nameFirst, String nameLast, UuidGenerator uuidGenerator, PersonRepository personRepository) {
        PersonName name = PersonName.create().setFirst(nameFirst).setLast(nameLast).build();
        Person person = Person.create(uuidGenerator.generate())
                .setDateOfBirth(LocalDate.now())
                .setName(name)
                .build();
        return personRepository.save(person);
    }
}
