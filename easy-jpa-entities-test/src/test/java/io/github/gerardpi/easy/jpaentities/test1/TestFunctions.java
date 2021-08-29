package io.github.gerardpi.easy.jpaentities.test1;

import io.github.gerardpi.easy.jpaentities.test1.domain.Person;
import io.github.gerardpi.easy.jpaentities.test1.domain.PersonName;
import io.github.gerardpi.easy.jpaentities.test1.domain.PersonRepository;

import java.time.LocalDate;

public final class TestFunctions {

    private TestFunctions() {
        // No instantation allowed.
    }

    public static Person storeAndReturnPerson(String nameFirst, String nameLast, LocalDate dateOfBirth, UuidGenerator uuidGenerator, PersonRepository personRepository) {
        PersonName name = PersonName.create().setFirst(nameFirst).setLast(nameLast).build();
        Person person = Person.create(uuidGenerator.generate())
                .setDateOfBirth(dateOfBirth)
                .setName(name)
                .build();
        return personRepository.save(person);
    }

    public static boolean matchesOrDoesNotMatch(String isOrIsNotEqual) {
        return textToBoolean("matches", "does not match", isOrIsNotEqual);
    }

    private static boolean textToBoolean(String trueText, String falseText, String textToCheck) {
        if (trueText.equals(textToCheck)) {
            return true;
        }
        if (falseText.equals(textToCheck)) {
            return false;
        }
        throw new IllegalArgumentException("Invalid value '" + textToCheck + "'. Must be either '" + trueText + "' (=true) or '" + falseText + "' (=false).");
    }
}
