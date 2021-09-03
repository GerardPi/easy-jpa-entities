package io.github.gerardpi.easy.jpaentities.test1;

import com.google.common.collect.ImmutableMap;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import io.github.gerardpi.easy.jpaentities.test1.domain.Person;
import io.github.gerardpi.easy.jpaentities.test1.domain.PersonName;
import io.github.gerardpi.easy.jpaentities.test1.json.ObjectMapperHolder;
import io.github.gerardpi.easy.jpaentities.test1.web.PersonDto;
import io.github.gerardpi.easy.jpaentities.test1.web.problem.RestApiMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.function.Supplier;

import static io.github.gerardpi.easy.jpaentities.test1.TestFunctions.storeAndReturnPerson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// Allow for overriding beans for testing purposes.
@TestPropertySource(properties = {TestConfig.BEAN_DEF_OVERRIDING_ENABLED})
// Order of configuration classes is important. Some beans are overridden.
@SpringBootTest(classes = {DemoApplication.class, TestConfig.class})
class PersonControllerTest extends SimpleScenarioTest<PersonControllerTest.State> {
    private static final Logger LOG = LoggerFactory.getLogger(PersonControllerTest.class);
    private final SavedEntities savedEntities = new SavedEntities();
    @Autowired
    private UuidGenerator uuidGenerator;
    @Autowired
    private Repositories repositories;
    @Autowired
    private Supplier<OffsetDateTime> testDateTimeSupplier;
    @ScenarioStage
    private State state;
    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void init() {
        ((FixedUuidSeriesGenerator) uuidGenerator).reset();
        repositories.clear();
        state.init(uuidGenerator, repositories, new MockMvcExecutor(wac), savedEntities,
                (TestDateTimeSupplier) testDateTimeSupplier);
    }

    @Test
    void get_person_not_found() {
        final OffsetDateTime givenDateTime = OffsetDateTime.parse("2021-09-01T21:11:28.0+02:00");
        given().the_the_current_date_and_time_is_$(givenDateTime);
        when().an_HTTP_$_on_$_with_the_id_for_entity_with_id_$_is_performed("GET", "/api/persons/",
                FixedUuidSeriesGenerator.generateWith(200).toString());
        then().the_HTTP_status_code_is_$(HttpStatus.NOT_FOUND)
                .and().the_response_contains_body_equals_$(
                        ObjectMapperHolder.getIntance().toJson(
                                RestApiMessageDto.create()
                                        .setTitle("item was not found")
                                        .setPath("/api/persons/00000200-1111-2222-3333-444444444444")
                                        .setMethod("GET")
                                        .setStatusCode(404)
                                        .setStatusSeries("CLIENT_ERROR")
                                        .setStatusName("Not Found")
                                        .addMessage("No 'Person' for ID '00000200-1111-2222-3333-444444444444' exists.")
                                        .setTimestamp(givenDateTime)
                                        .setTraceId("" + givenDateTime.toInstant().toEpochMilli())
                                        .build()));
    }

    @Test
    void get_persons() {
        given()
                .person_$_is_stored_in_the_database_with_first_name_$_and_last_name_$_and_date_of_birth_$_in_the_database(
                        1, "Frits", "Jansma", "2001-11-23")
                .and()
                .person_$_is_stored_in_the_database_with_first_name_$_and_last_name_$_and_date_of_birth_$_in_the_database(
                        2, "Albert", "Fles", "2002-11-24");
        when().an_HTTP_$_on_$_is_performed("GET", "/api/persons");
        then().the_HTTP_status_code_is_$(HttpStatus.OK)
                .and().the_number_of_items_received_is_$(2);
        when().an_HTTP_$_on_$_with_the_id_for_entity_$_is_performed("GET", "/api/persons/", 1);
        final Person person1 = repositories.getPersonRepository().findById(savedEntities.getPersonId(1)).get();
        then().the_response_contains_body_equals_$(
                        ObjectMapperHolder.getIntance().toJson(
                                PersonDto
                                        .from(person1)
                                        .setDateOfBirth(person1.getDateOfBirth())
                                        .setName(person1.getName())
                                        .build()))
                .and().in_the_response_$_is_equal_to_$("id", "00000000-1111-2222-3333-444444444444")
                .and().in_the_response_$_is_equal_to_$("etag", "0")
                .and().in_the_response_$_is_equal_to_$("name.first", "Frits")
                .and().in_the_response_$_is_equal_to_$("name.last", "Jansma")
                .and().in_the_response_$_is_equal_to_$("dateOfBirth", "2001-11-23")
                .and().the_HTTP_status_code_is_$(HttpStatus.OK)
                .and().no_exception_was_thrown();
        when().an_HTTP_$_on_$_with_the_id_for_entity_$_is_performed("GET", "/api/persons/", 2);

        final Person person2 = repositories.getPersonRepository().findById(savedEntities.getPersonId(2)).get();
        then().the_response_contains_body_equals_$(
                        ObjectMapperHolder.getIntance().toJson(
                                PersonDto
                                        .from(person2)
                                        .setDateOfBirth(person2.getDateOfBirth())
                                        .setName(person2.getName())
                                        .build()))
                .and().in_the_response_$_is_equal_to_$("id", "00000001-1111-2222-3333-444444444444")
                .and().in_the_response_$_is_equal_to_$("etag", "0")
                .and().in_the_response_$_is_equal_to_$("name.first", "Albert")
                .and().in_the_response_$_is_equal_to_$("name.last", "Fles")
                .and().in_the_response_$_is_equal_to_$("dateOfBirth", "2002-11-24")
                .and().the_HTTP_status_code_is_$(HttpStatus.OK)
                .and().no_exception_was_thrown();
    }

    @Test
    void post_person() {
        when().an_HTTP_$_on_$_is_performed_with_body_$("POST", "/api/persons",
                ObjectMapperHolder.getIntance().toJson(
                        PersonDto.create()
                                .setName(PersonName.create()
                                        .setFirst("first")
                                        .setLast("last")
                                        .build())
                                .setDateOfBirth(LocalDate.of(1998, 10, 22))
                                .build()));
        then().the_HTTP_status_code_is_$(HttpStatus.OK)
                .and().no_exception_was_thrown();
    }


    static class State extends Stage<State> {
        private SavedEntities savedEntities;
        private MockMvcExecutor mockMvcExecutor;
        private UuidGenerator uuidGenerator;
        private Repositories repositories;
        private Exception exception;
        private ResultActions resultActions;
        private TestDateTimeSupplier testDateTimeSupplier;

        private static MockMvc createMockMvc(final Object controller, final String uri) {
            return MockMvcBuilders.standaloneSetup(controller)
                    .defaultRequest(get(uri).accept(MediaType.APPLICATION_JSON))
                    .alwaysExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .build();
        }

        @Hidden
        void init(final UuidGenerator uuidGenerator,
                  final Repositories repositories,
                  final MockMvcExecutor mockMvcExecutor,
                  final SavedEntities savedEntities,
                  final TestDateTimeSupplier testDateTimeSupplier) {
            this.savedEntities = savedEntities;
            this.repositories = repositories;
            this.uuidGenerator = uuidGenerator;
            this.mockMvcExecutor = mockMvcExecutor;
            this.testDateTimeSupplier = testDateTimeSupplier;
        }

        State person_$_is_stored_in_the_database_with_first_name_$_and_last_name_$_and_date_of_birth_$_in_the_database(
                @Quoted final int testId, @Quoted final String nameFirst, @Quoted final String nameLast, @Quoted final String dateOfBirth) {
            final Person person = storeAndReturnPerson(nameFirst, nameLast, LocalDate.parse(dateOfBirth), uuidGenerator, repositories.getPersonRepository());
            this.savedEntities.putPersonId(testId, person.getId());
            return self();
        }

        State an_HTTP_$_on_$_is_performed(@Quoted final String httpMethod, @Quoted final String uri) {
            this.resultActions = mockMvcExecutor.executeHttpRequest(httpMethod, uri);
            return self();
        }

        State an_HTTP_$_on_$_with_the_id_for_entity_$_is_performed(@Quoted final String httpMethod, @Quoted final String uri, @Quoted final int testId) {
            an_HTTP_$_on_$_with_the_id_for_entity_with_id_$_is_performed(httpMethod, uri, savedEntities.getPersonId(testId).toString());
            return self();
        }

        State an_HTTP_$_on_$_with_the_id_for_entity_with_id_$_is_performed(@Quoted final String httpMethod, @Quoted final String uri, @Quoted final String id) {
            this.resultActions = mockMvcExecutor.executeHttpRequest(httpMethod, uri + "/" + id);
            return self();
        }

        State the_HTTP_status_code_is_$(@Quoted final HttpStatus httpStatus) {
            assertThat(resultActions).isNotNull();
            assertThat(resultActions.andReturn().getResponse().getStatus()).isEqualTo(httpStatus.value());
            return self();
        }

        State no_exception_was_thrown() {
            assertThat(this.exception).isNull();
            return self();
        }

        State in_the_response_$_is_equal_to_$(@Quoted final String jsonPath, @Quoted final String expectedValue) {
            try {
                resultActions.andExpect(jsonPath(jsonPath, is(expectedValue)));
            } catch (final Exception e) {
                LOG.info("Caught exception '{}': '{}'" + e.getClass().getName(), e.getMessage());
                this.exception = e;
            }

            return self();
        }

        State the_number_of_items_received_is_$(final int expectedSize) {
            try {
                resultActions
                        .andExpect(jsonPath("$.content").isArray())
                        .andExpect(jsonPath("$.content", hasSize(expectedSize))
                        );
            } catch (final Exception e) {
                LOG.info("Caught exception '{}': '{}'" + e.getClass().getName(), e.getMessage());
                this.exception = e;
            }
            return self();
        }

        State an_HTTP_$_on_$_is_performed_with_body_$(@Quoted final String httpMethod, @Quoted final String uri, @Format(JgivenJsonPrettyFormatter.class) final String personDtoJson) {
            //this.resultActions = mockMvcExecutor.executeHttpRequest(httpMethod, uri, personDtoJson);
            this.resultActions = mockMvcExecutor.executeHttpRequest(httpMethod, uri,
                    ImmutableMap.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE),
                    personDtoJson);
            return self();
        }

        State the_response_contains_body_equals_$(@Format(JgivenJsonPrettyFormatter.class) final String expectedJson) {
            try {
                assertThat(this.resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8))
                        .isEqualTo(expectedJson);
            } catch (final UnsupportedEncodingException e) {
                LOG.info("Caught exception '{}': '{}'" + e.getClass().getName(), e.getMessage());
                this.exception = e;
            }
            return self();
        }

        State the_the_current_date_and_time_is_$(@Quoted final OffsetDateTime givenDateTime) {
            testDateTimeSupplier.fixDateTime(givenDateTime);
            return self();
        }

        State the_the_current_date_and_time_is_$(final String givenDateTime) {
            testDateTimeSupplier.fixDateTime(OffsetDateTime.parse(givenDateTime));
            return self();
        }
    }
}
