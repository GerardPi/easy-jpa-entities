package io.github.gerardpi.easy.jpaentities.test1;

import io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.Person;
import io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonName;
import io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonRepository;

import java.time.LocalDate;

public final class TestFunctions {

    private TestFunctions() {
        // No instantation allowed.
    }

    public static Person storeAndReturnPerson(final String nameFirst, final String nameLast, final LocalDate dateOfBirth, final UuidGenerator uuidGenerator, final PersonRepository personRepository) {
        final PersonName name = PersonName.create().setFirst(nameFirst).setLast(nameLast).build();
        final Person person = Person.create(uuidGenerator.generate())
                .setDateOfBirth(dateOfBirth)
                .setName(name)
                .build();
        return personRepository.save(person);
    }

    public static boolean matchesOrDoesNotMatch(final String isOrIsNotEqual) {
        return textToBoolean("matches", "does not match", isOrIsNotEqual);
    }

    private static boolean textToBoolean(final String trueText, final String falseText, final String textToCheck) {
        if (trueText.equals(textToCheck)) {
            return true;
        }
        if (falseText.equals(textToCheck)) {
            return false;
        }
        throw new IllegalArgumentException("Invalid value '" + textToCheck + "'. Must be either '" + trueText + "' (=true) or '" + falseText + "' (=false).");
    }
}
