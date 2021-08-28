package io.github.gerardpi.easy.jpaentities.test1;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import io.github.gerardpi.easy.jpaentities.test1.domain.PersonName;
import io.github.gerardpi.easy.jpaentities.test1.json.ObjectMapperHolder;
import io.github.gerardpi.easy.jpaentities.test1.web.PersonDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class SerialisationTest extends SimpleScenarioTest<SerialisationTest.State> {
    @Test
    public void testToJson() {
        given().a_PersonDto_with_first_name_$_and_last_name_$_and_date_of_birth_$("Frits", "Jansma", "2001-11-21");
        when().that_PersonDto_is_serialized_to_JSON();
        then().the_result_JSON_is_$(
                ObjectMapperHolder.getIntance().toJson(
                        PersonDto.create()
                                .setName(PersonName.create()
                                        .setFirst("Frits")
                                        .setLast("Jansma")
                                        .build())
                                .setDateOfBirth(LocalDate.of(2001, 11, 21))
                                .build()));
    }

    @Test
    public void testFromJson() {
        when().JSON_$_is_deserialized_into_a_$(
                ObjectMapperHolder.getIntance().toJson(
                        PersonDto.create()
                                .setName(PersonName.create()
                                        .setFirst("Kees")
                                        .setLast("Fritz")
                                        .build())
                                .setDateOfBirth(LocalDate.of(1998, 10, 22))
                                .build()), PersonDto.class);
        then().that_PersonDto_has_first_name_$_and_last_name_$_and_date_of_birth_$("Kees", "Fritz", "1998-10-22");
    }

    static class State extends Stage<State> {
        private PersonDto personDto;
        private String personDtoJson;

        State a_PersonDto_with_first_name_$_and_last_name_$_and_date_of_birth_$(@Quoted String nameFirst, @Quoted String nameLast, @Quoted String dateOfBirthStr) {
            LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr);
            this.personDto = PersonDto.create().setName(PersonName.create().setFirst(nameFirst).setLast(nameLast).build()).setDateOfBirth(dateOfBirth).build();
            return self();
        }

        State that_PersonDto_is_serialized_to_JSON() {
            this.personDtoJson = ObjectMapperHolder.getIntance().toJson(personDto);
            return self();
        }

        State JSON_$_is_deserialized_into_a_$(@Format(JgivenJsonPrettyFormatter.class) String personDtoJson, Class<?> targetClass) {
            this.personDto = ObjectMapperHolder.getIntance().fromJson(personDtoJson, PersonDto.class);
            return self();
        }

        State the_result_JSON_is_$(@Format(JgivenJsonPrettyFormatter.class) String expectedJson) {
            assertThat(this.personDtoJson).isEqualTo(expectedJson);
            return self();
        }

        State that_PersonDto_has_first_name_$_and_last_name_$_and_date_of_birth_$(
                @Quoted String expectedFirstName, @Quoted String expectedLastName, @Quoted String expectedDateOfBirth) {
            assertThat(this.personDto.getName().getFirst()).isEqualTo(expectedFirstName);
            assertThat(this.personDto.getName().getLast()).isEqualTo(expectedLastName);
            assertThat(this.personDto.getDateOfBirth()).isEqualTo(LocalDate.parse(expectedDateOfBirth));
            return self();
        }
    }

}
