package io.github.gerardpi.easy.jpaentities.test1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.jgiven.Stage;
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
    public void test() {
        given().a_PersonDto_with_first_name_$_and_last_name_$_and_date_of_birth_$("Frits", "Jansma", "2001-11-21");
        when().that_PersonDto_is_serialized_to_JSON();
        then().the_result_is_$("{\"name\":{\"first\":\"Frits\",\"last\":\"Jansma\"},\"dateOfBirth\":\"2001-11-21\"}");
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

        State the_result_is_$(@Quoted String expectedJson) {
            assertThat(this.personDtoJson).isEqualTo(expectedJson);
            return self();
        }
    }

}
