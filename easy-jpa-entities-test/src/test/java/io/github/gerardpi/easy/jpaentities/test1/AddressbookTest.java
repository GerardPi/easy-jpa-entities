package io.github.gerardpi.easy.jpaentities.test1;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(SpringProfile.TEST)
@SpringBootTest
public class AddressbookTest extends SimpleScenarioTest<AddressbookTest.State> {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PersonAddressRepository personAddressRepository;
    @Autowired
    private UuidGenerator uuidGenerator;
    @ScenarioStage
    private State state;

    @BeforeEach
    public void init() {
        ((FixedUuidSeriesGenerator) uuidGenerator).reset();
        state.init(uuidGenerator, personAddressRepository, addressRepository, personRepository);
    }

    @Test
    public void optimisticLockingVersionNumberIncreasesWithUpdates() {
        when().person_$_is_created_with_first_name_$_and_last_name_$_in_the_database(1, "Frits", "Jansma");
        then().that_$_$_has_ID_$(Person.class, 1, "00000000-1111-2222-3333-444444444444");
        then().that_$_with_number_$_has_optimistic_locking_version_number_$(Person.class, 1, 0);
        when().updating_a_person_$_with_first_name_$(1, "Klaas");
        then().that_$_with_number_$_has_optimistic_locking_version_number_$(Person.class, 1, 1);
        when().updating_a_person_$_with_first_name_$(1, "Piet");
        then().that_$_with_number_$_has_optimistic_locking_version_number_$(Person.class, 1, 2);
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
        private final SortedMap<Integer, UUID> savedPersons = new TreeMap<>();
        private final SortedMap<Integer, UUID> savedAddresses = new TreeMap<>();
        private final SortedMap<Integer, UUID> savedPersonAddresses = new TreeMap<>();
        private AddressRepository addressRepository;
        private PersonRepository personRepository;
        private PersonAddressRepository personAddressRepository;
        private UuidGenerator uuidGenerator;

        @Hidden
        void init(UuidGenerator uuidGenerator, PersonAddressRepository personAddressRepository, AddressRepository addressRepository, PersonRepository personRepository) {
            this.uuidGenerator = uuidGenerator;
            this.addressRepository = addressRepository;
            this.personRepository = personRepository;
            this.personAddressRepository = personAddressRepository;
            this.personAddressRepository.deleteAll();
            this.addressRepository.deleteAll();
            this.personRepository.deleteAll();
        }

        State person_$_is_created_with_first_name_$_and_last_name_$_in_the_database(@Quoted int number, @Quoted String nameFirst, @Quoted String nameLast) {
            Person person = Person.create(uuidGenerator.generate())
                    .setDateOfBirth(LocalDate.now())
                    .setNameFirst(nameFirst)
                    .setNameLast(nameLast)
                    .build();
            this.savedPersons.put(number, personRepository.save(person).getId());
            return self();
        }

        State that_$_$_has_ID_$(Class<?> entityClass, int number, @Quoted String expectedId) {
            switch (entityClass.getSimpleName()) {
                case "Person":
                    Person person = personRepository.findById(savedPersons.get(number)).get();
                    assertThat(this.savedPersons.get(number).toString()).isEqualTo(expectedId);
                    break;
                case "Address":
                    Address address = addressRepository.findById(savedAddresses.get(number)).get();
                    assertThat(this.savedAddresses.get(number).toString()).isEqualTo(expectedId);
                    break;
                default:
                    throw new IllegalStateException("No clue what to do");
            }
            return self();
        }

        State that_$_with_number_$_has_optimistic_locking_version_number_$(
                Class<?> entityClass, int number, @Quoted int expectedOptLockVersion) {
            assertThat(getEntity(entityClass, number).getOptLockVersion()).isEqualTo(expectedOptLockVersion);
            return self();
        }

        OptLockablePersistable getEntity(Class<?> entityClass, int number) {
            switch (entityClass.getSimpleName()) {
                case "Person":
                    return personRepository.findById(savedPersons.get(number)).get();
                case "Address":
                    return addressRepository.findById(savedAddresses.get(number)).get();
                case "PersonAddress":
                    return personAddressRepository.findById(savedPersonAddresses.get(number)).get();
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
            this.savedAddresses.put(number, addressRepository.save(address).getId());
            return self();
        }

        State updating_a_person_$_with_first_name_$(int number, @Quoted String newNameFirst) {
            Person person = personRepository.findById(savedPersons.get(number)).get();
            personRepository.save(person.modify().setNameFirst(newNameFirst).build());
            return self();
        }

        State updating_an_address_$_with_$_$(int number, @Quoted String propName, @Quoted String newValue) {
            Address address = addressRepository.findById(savedAddresses.get(number)).get();
            switch (propName) {
                case "postalCode":
                    addressRepository.save(address.modify().setPostalCode(newValue).build());
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
                    .setPersonId(savedPersons.get(personNumber))
                    .setAddressId(savedAddresses.get(addressNumber));
            personAddressTypes.forEach(type -> builder.addType(PersonAddressType.valueOf(type)));
            PersonAddress personAddress = personAddressRepository.save(builder.build());
            savedPersonAddresses.put(personAddressNumber, personAddress.getId());
            return self();
        }

        State the_person_$_can_be_found_via_address_$_using_postal_code_$_and_house_number(
                @Quoted int personNumber, @Quoted int addressNumber, @Quoted String countryCode, @Quoted String postalCode, @Quoted String houseNumber) {
            Address address = addressRepository.findByCountryCodeAndPostalCodeAndHouseNumber(countryCode, postalCode, houseNumber).get();
            assertThat(savedAddresses.get(addressNumber)).isEqualTo(address.getId());

            List<PersonAddress> personAddresses = personAddressRepository.findByAddressId(address.getId());
            Assertions.assertThat(personAddresses).hasSize(1);
            PersonAddress personAddress = personAddresses.get(0);
            Person person = personRepository.findById(personAddress.getPersonId()).get();
            assertThat(savedPersons.get(personNumber)).isEqualTo(person.getId());
            return self();
        }

    }

}
