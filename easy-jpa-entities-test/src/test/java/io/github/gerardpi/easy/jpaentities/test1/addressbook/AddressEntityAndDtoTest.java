package io.github.gerardpi.easy.jpaentities.test1.addressbook;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import io.github.gerardpi.easy.jpaentities.test1.FixedUuidSeriesGenerator;
import io.github.gerardpi.easy.jpaentities.test1.JgivenObjectPrettyFormatter;
import io.github.gerardpi.easy.jpaentities.test1.UuidGenerator;
import io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.Address;
import io.github.gerardpi.easy.jpaentities.test1.web.addressbook.AddressDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressEntityAndDtoTest extends SimpleScenarioTest<AddressEntityAndDtoTest.State> {
    private final UuidGenerator uuidGenerator = new FixedUuidSeriesGenerator();

    @Test
    public void dtoToEntityCopiesAllFieldsFromDtoRegardless() {
        final Address address = Address.create(uuidGenerator.generate())
                .setCountryCode("NL")
                .setCity("Amsterdam")
                .setPostalCode("1234AB")
                .setStreet("this street")
                .setHouseNumber("1a")
                .build();
        given().an_address_$(address);
        final AddressDto dto = AddressDto.fromEntity(address)
                .setCountryCode(null)
                .setCity(null)
                .setStreet("different street")
                .build();
        when().a_DTO_$_is_used_to_modify_that_address_entity_copying_values_from_all_DTO_fields(dto);
        final Address expectedAddress = address.modify()
                .setCountryCode(null)
                .setCity(null)
                .setPostalCode("1234AB")
                .setStreet("different street")
                .setHouseNumber("1a")
                .build();
        then().the_resulting_address_entity_is_$(expectedAddress)
                .comment(
                        "Note that the etag is always null since this entity was never stored; " +
                                "and the isModified values is always true since the entity was never stored.");
    }

    @Test
    public void dtoToEntityNotNullCopiesNotNullFieldsFromDto() {
        final Address address = Address.create(uuidGenerator.generate())
                .setCountryCode("NL")
                .setCity("Amsterdam")
                .setPostalCode("1234AB")
                .setStreet("this street")
                .setHouseNumber("1a")
                .build();
        given().an_address_$(address);
        final AddressDto dto = AddressDto.fromEntity(address)
                .setCountryCode(null)
                .setCity(null)
                .setStreet("different street")
                .build();
        when().a_DTO_$_is_used_to_modify_that_address_entity_copying_values_from_DTO_fields_that_are_not_null(dto);
        final Address expectedAddress = address.modify()
                .setCountryCode("NL")
                .setCity("Amsterdam")
                .setPostalCode("1234AB")
                .setStreet("different street")
                .setHouseNumber("1a")
                .build();
        then().the_resulting_address_entity_is_$(expectedAddress)
                .comment(
                        "Note that the etag is always null since this entity was never stored; " +
                                "and the isModified values is always true since the entity was never stored.");
    }

    static class State extends Stage<State> {
        private Address address;
        private Address modifiedAddress;
        private AddressDto addressDto;


        State an_address_$(final @Format(JgivenObjectPrettyFormatter.class) Address givenAddress) {
            this.address = givenAddress;
            return self();
        }

        State a_DTO_$_is_used_to_modify_that_address_entity_copying_values_from_all_DTO_fields(
                final @Format(JgivenObjectPrettyFormatter.class) AddressDto dto) {
            this.modifiedAddress = dto.toEntity(address).build();
            return self();
        }

        State a_DTO_$_is_used_to_modify_that_address_entity_copying_values_from_DTO_fields_that_are_not_null(
                final @Format(JgivenObjectPrettyFormatter.class) AddressDto dto) {
            this.modifiedAddress = dto.toEntityNotNull(address).build();
            return self();
        }

        State the_resulting_address_entity_is_$(final @Format(JgivenObjectPrettyFormatter.class) Address expectedAddress) {
            assertThat(this.modifiedAddress.getCountryCode()).isEqualTo(expectedAddress.getCountryCode());
            assertThat(this.modifiedAddress.getCity()).isEqualTo(expectedAddress.getCity());
            assertThat(this.modifiedAddress.getPostalCode()).isEqualTo(expectedAddress.getPostalCode());
            assertThat(this.modifiedAddress.getStreet()).isEqualTo(expectedAddress.getStreet());
            assertThat(this.modifiedAddress.getHouseNumber()).isEqualTo(expectedAddress.getHouseNumber());
            return self();
        }
    }
}
