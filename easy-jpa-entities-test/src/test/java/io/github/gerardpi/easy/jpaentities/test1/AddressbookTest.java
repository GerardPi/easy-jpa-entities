package io.github.gerardpi.easy.jpaentities.test1;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import io.github.gerardpi.easy.jpaentities.test1.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.github.gerardpi.easy.jpaentities.test1.TestFunctions.storeAndReturnPerson;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(SpringProfile.TEST)
@SpringBootTest
public class AddressbookTest extends SimpleScenarioTest<AddressbookTest.State> {
    @Autowired
    private Repositories repositories;
    @Autowired
    private UuidGenerator uuidGenerator;
    @ScenarioStage
    private State state;

    @BeforeEach
    public void init() {
        ((FixedUuidSeriesGenerator) uuidGenerator).reset();
        // Repositories repositories = new Repositories(personRepository, addressRepository, personAddressRepository, itemRepository, itemOrderRepository, itemOrderLineRepository);
        state.init(uuidGenerator, repositories);
    }

    @Test
    public void optimisticLockingVersionNumberIncreasesWithUpdates() {
        when().person_$_is_created_with_first_name_$_and_last_name_$_in_the_database(1, "Frits", "Jansma");
        then().that_$_$_has_ID_$(Person.class, 1, "00000000-1111-2222-3333-444444444444");
        then().that_$_with_number_$_has_optimistic_locking_version_number_$(Person.class, 1, 0);
        when().updating_a_person_$_with_first_name_$(1, "Klaas")
                .and()
                .updating_a_person_$_with_date_of_birth_$(1, "1985-01-01");
        then().that_$_with_number_$_has_optimistic_locking_version_number_$(Person.class, 1, 2).and().the_person_with_key_$_has_date_of_birth_$(1,"1985-01-01");
        when().updating_a_person_$_with_first_name_$(1, "Piet");
        then().that_$_with_number_$_has_optimistic_locking_version_number_$(Person.class, 1, 3);
        when().creating_an_address_$_with_data_$_$_$_$_$(1, "NL", "Amsterdam", "1234AA", "Damstraat", "1");
        then().that_$_$_has_ID_$(Address.class, 1, "00000001-1111-2222-3333-444444444444")
                .and().that_$_with_number_$_has_optimistic_locking_version_number_$(Address.class, 1, 0);
        when().updating_an_address_$_with_$_$(1, "postalCode", "1234AB");
        then().that_$_with_number_$_has_optimistic_locking_version_number_$(Address.class, 1, 1);
    }

    @Test
    public void personAddressCanBeUsedToLinkAPersonToAnAddress() {
        given().person_$_is_created_with_first_name_$_and_last_name_$_in_the_database(1, "Frits", "Jansma")
                .and().creating_an_address_$_with_data_$_$_$_$_$(1, "NL", "Amsterdam", "1234AA", "Damstraat", "1");
        when().a_relation_is_created_$_between_person_$_and_address_$_with_types(1, 1, 1, Arrays.asList("RESIDENCE", "PROPERTY"));
        then().the_person_$_can_be_found_via_address_$_using_postal_code_$_and_house_number(1, 1, "NL" ,"1234AA", "1");
    }

    static class State extends Stage<State> {
        private final SavedEntities savedEntities = new SavedEntities();
        private Repositories repositories;
        private UuidGenerator uuidGenerator;

        @Hidden
        void init(UuidGenerator uuidGenerator, Repositories repositories) {
            this.uuidGenerator = uuidGenerator;
            this.repositories = repositories;
            repositories.clear();
        }

        State person_$_is_created_with_first_name_$_and_last_name_$_in_the_database(@Quoted int number, @Quoted String nameFirst, @Quoted String nameLast) {
            Person person = storeAndReturnPerson(nameFirst, nameLast, uuidGenerator, repositories.getPersonRepository());
            this.savedEntities.putPersonId(number, person.getId());
            return self();
        }

        <T> State that_$_$_has_ID_$(Class<T> entityClass, int number, @Quoted String expectedId) {
            switch (entityClass.getSimpleName()) {
                case "Person":
                    Person person = repositories.getPersonRepository().findById(savedEntities.getPersonId(number)).get();
                    assertThat(this.savedEntities.getPersonId(number).toString()).isEqualTo(expectedId);
                    break;
                case "Address":
                    Address address = repositories.getAddressRepository().findById(savedEntities.getAddressId(number)).get();
                    assertThat(this.savedEntities.getAddressId(number).toString()).isEqualTo(expectedId);
                    break;
                default:
                    throw new IllegalStateException("No clue what to do");
            }
            return self();
        }

        State that_$_with_number_$_has_optimistic_locking_version_number_$(
                Class<?> entityClass, int number, @Quoted int expectedOptLockVersion) {
            assertThat(getEntity(entityClass, number).getEtag()).isEqualTo(expectedOptLockVersion);
            return self();
        }

        PersistableEntityWithTag getEntity(Class<?> entityClass, int number) {
            switch (entityClass.getSimpleName()) {
                case "Person":
                    return repositories.getPersonRepository().findById(savedEntities.getPersonId(number)).get();
                case "Address":
                    return repositories.getAddressRepository().findById(savedEntities.getAddressId(number)).get();
                case "PersonAddress":
                    return repositories.getPersonAddressRepository().findById(savedEntities.getPersonAddressId(number)).get();
                default:
                    throw new IllegalStateException("Don't know entity class '" + entityClass.getName() + "'");
            }
        }

        State creating_an_address_$_with_data_$_$_$_$_$(
                int number,
                @Quoted String countryCode,
                @Quoted String city,
                @Quoted String postalCode,
                @Quoted String street,
                @Quoted String houseNumber) {
            Address address = Address.create(uuidGenerator.generate())
                    .setCountryCode(countryCode)
                    .setCity(city)
                    .setPostalCode(postalCode)
                    .setStreet(street)
                    .setHouseNumber(houseNumber)
                    .build();
            this.savedEntities.putAddressId(number, repositories.getAddressRepository().save(address).getId());
            return self();
        }

        State updating_a_person_$_with_first_name_$(int number, @Quoted String newNameFirst) {
            Person person = repositories.getPersonRepository().findById(savedEntities.getPersonId(number)).get();
            PersonName newName = person.getName().modify().setFirst(newNameFirst).build();
            repositories.getPersonRepository().save(person.modify().setName(newName).build());
            return self();
        }

        State updating_a_person_$_with_date_of_birth_$(int personKey, String newDateOfBirth) {
            Person person = repositories.getPersonRepository().findById(savedEntities.getPersonId(personKey)).get();
            repositories.getPersonRepository().save(person.modify().setDateOfBirth(LocalDate.parse(newDateOfBirth, DateTimeFormatter.ISO_DATE)).build());
            return self();
        }
        State the_person_with_key_$_has_date_of_birth_$(int personKey, String expectedDateOfBirth) {
            Person person = repositories.getPersonRepository().findById(savedEntities.getPersonId(personKey)).get();
            assertThat(person.getDateOfBirth()).isEqualTo(LocalDate.parse(expectedDateOfBirth, DateTimeFormatter.ISO_DATE));
            return self();
        }

        State updating_an_address_$_with_$_$(int number, @Quoted String propName, @Quoted String newValue) {
            Address address = repositories.getAddressRepository().findById(savedEntities.getAddressId(number)).get();
            switch (propName) {
                case "postalCode":
                    repositories.getAddressRepository().save(address.modify().setPostalCode(newValue).build());
                    break;
                default:
                    throw new IllegalStateException("No clue what to do");
            }
            return self();
        }

        State a_relation_is_created_$_between_person_$_and_address_$_with_types(
                @Quoted int personAddressNumber,
                @Quoted int personNumber, @Quoted int addressNumber, @Quoted List<String> personAddressTypes) {
            PersonAddress.Builder builder = PersonAddress.create(uuidGenerator.generate())
                    .setPersonId(savedEntities.getPersonId(personNumber))
                    .setAddressId(savedEntities.getAddressId(addressNumber));
            personAddressTypes.forEach(type -> builder.addType(PersonAddressType.valueOf(type)));
            PersonAddress personAddress = repositories.getPersonAddressRepository().save(builder.build());
            savedEntities.putPersonAddressId(personAddressNumber, personAddress.getId());
            return self();
        }

        State the_person_$_can_be_found_via_address_$_using_postal_code_$_and_house_number(
                @Quoted int personNumber, @Quoted int addressNumber, @Quoted String countryCode, @Quoted String postalCode, @Quoted String houseNumber) {
            Address address = repositories.getAddressRepository().findByCountryCodeAndPostalCodeAndHouseNumber(countryCode, postalCode, houseNumber).get();
            assertThat(savedEntities.getAddressId(addressNumber)).isEqualTo(address.getId());

            List<PersonAddress> personAddresses = repositories.getPersonAddressRepository().findByAddressId(address.getId());
            Assertions.assertThat(personAddresses).hasSize(1);
            PersonAddress personAddress = personAddresses.get(0);
            Person person = repositories.getPersonRepository().findById(personAddress.getPersonId()).get();
            assertThat(savedEntities.getPersonId(personNumber)).isEqualTo(person.getId());
            return self();
        }

    }

}
