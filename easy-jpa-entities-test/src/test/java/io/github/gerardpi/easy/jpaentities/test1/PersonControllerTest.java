package io.github.gerardpi.easy.jpaentities.test1;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import io.github.gerardpi.easy.jpaentities.test1.domain.Person;
import io.github.gerardpi.easy.jpaentities.test1.web.PersonController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static io.github.gerardpi.easy.jpaentities.test1.TestFunctions.storeAndReturnPerson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// Allow for overriding beans for testing purposes.
@TestPropertySource(properties = { TestConfig.BEAN_DEF_OVERRIDING_ENABLED })
// Order of configuration classes is important. Some beans are overridden.
@SpringBootTest(classes = {DemoApplication.class, TestConfig.class})
class PersonControllerTest extends SimpleScenarioTest<PersonControllerTest.State> {
    private static final Logger LOG = LoggerFactory.getLogger(PersonControllerTest.class);

    @Autowired
    private UuidGenerator uuidGenerator;
    @Autowired
    private Repositories repositories;
    @ScenarioStage
    private State state;
    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void init() {
        repositories.clear();
        state.init(uuidGenerator, repositories, IntegrationTestUtils.createMockMvc(wac));
    }

    @Test
    public void get_persons() {
        given()
                .person_$_is_stored_in_the_database_with_first_name_$_and_last_name_$_in_the_database(1, "Frits", "Jansma")
                .and()
                .person_$_is_stored_in_the_database_with_first_name_$_and_last_name_$_in_the_database(2, "Albert", "Fles");
        when().an_HTTP_$_on_$_is_performed("GET", "/api/persons");
        then().the_HTTP_status_code_is_$(HttpStatus.OK)
                .and()
                .the_number_of_items_received_is_$(2);
        when().an_HTTP_$_on_$_with_the_id_for_entity_$_is_performed("GET", "/api/persons/", 1);
//        when().executing_HTTP_$_on_person_$_with_JSON_$("PATCH", 2, "{")
    }


    static class State extends Stage<State> {
        private final SavedEntities savedEntities = new SavedEntities();
        private MockMvc mockMvc;
        private UuidGenerator uuidGenerator;
        private Repositories repositories;
        private Exception exception;
        private ResultActions resultActions;
        private MvcResult mvcResult;

        private static MockMvc createMockMvc(Object controller, String uri) {
            return MockMvcBuilders.standaloneSetup(controller)
                    .defaultRequest(get(uri).accept(MediaType.APPLICATION_JSON))
                    .alwaysExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .build();

        }

        @Hidden
        void init(UuidGenerator uuidGenerator, Repositories repositories, MockMvc mockMvc) {
            this.repositories = repositories;
            this.uuidGenerator = uuidGenerator;
            this.mockMvc = mockMvc;
//            mockMvc = createMockMvc(new PersonController(uuidGenerator, repositories.getPersonRepository()), "/api/persons");
        }

        State person_$_is_stored_in_the_database_with_first_name_$_and_last_name_$_in_the_database(@Quoted int testId, @Quoted String nameFirst, @Quoted String nameLast) {
            Person person = storeAndReturnPerson(nameFirst, nameLast, uuidGenerator, repositories.getPersonRepository());
            this.savedEntities.putPersonId(testId, person.getId());
            return self();
        }

        State an_HTTP_$_on_$_is_performed(@Quoted String httpMethod, @Quoted String uri) {
            performHttpRequest(httpMethod, uri);
            return self();
        }

        State an_HTTP_$_on_$_with_the_id_for_entity_$_is_performed(String httpMethod, String uri, int testId) {
            performHttpRequest(httpMethod, uri + "/" + savedEntities.getPersonId(testId));
            return self();
        }

        void performHttpRequest(String httpMethod, String uri) {
            try {
                this.resultActions = mockMvc.perform(createRequestBuilder(httpMethod, uri)).andDo(MockMvcResultHandlers.print());
                this.mvcResult = resultActions.andReturn();
            } catch (Exception e) {
                LOG.info("Caught exception '{}': '{}'" + e.getClass().getName(), e.getMessage());
                this.exception = e;
            }
        }

        RequestBuilder createRequestBuilder(String httpMethod, String uri) {
            return createRequestBuilder(httpMethod, uri, null);

        }

        RequestBuilder createRequestBuilder(String httpMethod, String uri, String body) {
            switch (httpMethod) {
                case "GET":
                case "DELETE":
                    return get(uri)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .characterEncoding(StandardCharsets.UTF_8.displayName());
                case "POST":
                case "PUT":
                case "PATCH":
                    return post(uri)
                            .content(body)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .characterEncoding(StandardCharsets.UTF_8.displayName());
            }
            throw new IllegalArgumentException("Don't know what to do with '" + httpMethod + "'");
        }

        State the_HTTP_status_code_is_$(@Quoted HttpStatus httpStatus) {
            assertThat(this.resultActions).isNotNull();
            assertThat(mvcResult.getResponse().getStatus()).isEqualTo(httpStatus.value());
            return self();
        }

        State the_number_of_items_received_is_$(int expectedSize) {
            try {
                resultActions
                        .andExpect(jsonPath("$.content").isArray())
                        .andExpect(jsonPath("$.content", hasSize(expectedSize))
                        );
            } catch (Exception e) {
                LOG.info("Caught exception '{}': '{}'" + e.getClass().getName(), e.getMessage());
                this.exception = e;
            }
            return self();
        }
    }
}
