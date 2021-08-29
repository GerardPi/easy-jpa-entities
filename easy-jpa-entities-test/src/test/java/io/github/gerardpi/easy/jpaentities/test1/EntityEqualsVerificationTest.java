package io.github.gerardpi.easy.jpaentities.test1;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import io.github.gerardpi.easy.jpaentities.test1.domain.Address;
import io.github.gerardpi.easy.jpaentities.test1.domain.Currency;
import io.github.gerardpi.easy.jpaentities.test1.domain.Person;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test uses EqualsVerifier.
 * For more details, refer to https://jqno.nl/equalsverifier/
 */
class EntityEqualsVerificationTest extends SimpleScenarioTest<EntityEqualsVerificationTest.State> {
    @Test
    void check_that_the_id_is_the_significant_part_that_is_used_for_equals_and_hashCode_in_entity_Person() {
        given().an_entity_class_$(Person.class)
                .which_has_a_field_$_that_is_never_null("id")
                .and()
                .which_has_a_field_$_that_is_ignored_by_equals_method("etag")
                .and()
                .which_has_a_field_$_that_is_ignored_by_equals_method("name")
                .and()
                .which_has_a_field_$_that_is_ignored_by_equals_method("dateOfBirth");
        when().verifying_that_entity_class();
        then().that_entity_class_is_ok();
    }

    @Test
    void check_that_the_id_is_the_significant_part_that_is_used_for_equals_and_hashCode_in_entity_Address() {
        given().an_entity_class_$(Address.class)
                .which_has_a_field_$_that_is_never_null("id")
                .and()
                .which_has_a_field_$_that_is_ignored_by_equals_method("etag")
                .and()
                .which_has_a_field_$_that_is_ignored_by_equals_method("countryCode")
                .and()
                .which_has_a_field_$_that_is_ignored_by_equals_method("city")
                .and()
                .which_has_a_field_$_that_is_ignored_by_equals_method("postalCode")
                .and()
                .which_has_a_field_$_that_is_ignored_by_equals_method("street")
                .and()
                .which_has_a_field_$_that_is_ignored_by_equals_method("houseNumber");
        when().verifying_that_entity_class();
        then().that_entity_class_is_ok();
    }

    @Test
    void check_that_the_id_is_the_significant_part_that_is_used_for_equals_and_hashCode_in_entity_Currency() {
        given().an_entity_class_$(Currency.class)
                .which_has_a_field_$_that_is_never_null("id")
                .and()
                .which_does_not_possess_a_field_$("etag")
                .and()
                .which_has_a_field_$_that_is_ignored_by_equals_method("name")
                .and()
                .which_has_a_field_$_that_is_ignored_by_equals_method("code");
        when().verifying_that_entity_class();
        then().that_entity_class_is_ok();
    }

    static class State extends Stage<State> {
        private Class<?> entityClass;
        private SingleTypeEqualsVerifierApi<?> entityClassVerifyer;
        private final List<String> fieldsIgnoredInEquals = new ArrayList<>();
        private AssertionError assertionError;

        State an_entity_class_$(@Quoted Class<?> entityClass) {
            this.entityClass = entityClass;
            this.entityClassVerifyer = EqualsVerifier.forClass(entityClass);
            return self();
        }

        State which_has_a_field_$_that_is_never_null(@Quoted String neverNullField) {
            this.entityClassVerifyer.withNonnullFields(neverNullField);
            return self();
        }

        State which_has_a_field_$_that_is_ignored_by_equals_method(@Quoted String fieldIgnoredInEquals) {
            this.fieldsIgnoredInEquals.add(fieldIgnoredInEquals);
            return self();
        }

        State which_does_not_possess_a_field_$(@Quoted String fieldNotPresent) {
            try {
                List<String> fields = Stream.of(this.entityClass.getDeclaredFields())
                        .map(Field::toString)
                        .collect(Collectors.toList());
                assertThat(fields.contains(fieldNotPresent)).isFalse();
            } catch (SecurityException e) {
                throw new IllegalStateException(e);
            }
            return self();
        }

        State verifying_that_entity_class() {
            entityClassVerifyer.withIgnoredFields(fieldsIgnoredInEquals.toArray(new String[]{}));
            try {
                entityClassVerifyer.verify();
            } catch (AssertionError assertionError) {
                this.assertionError = assertionError;
            }
            return self();
        }

        State that_entity_class_is_ok() {
            assertThat(this.assertionError).isNull();
            return self();
        }
    }
}
