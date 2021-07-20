package io.github.gerardpi.easy.jpaentities.test1;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import io.github.gerardpi.easy.jpaentities.test1.web.PersonController;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(SpringProfile.TEST)
@SpringBootTest
class PersonControllerTest extends SimpleScenarioTest<PersonControllerTest.State> {
    private static final Logger LOG = LoggerFactory.getLogger(PersonControllerTest.class);

    @Autowired
    private UuidGenerator uuidGenerator;
    @Autowired
    private Repositories repositories;

    @Transactional
    @Test
    public void test() {

    }

    static class State extends Stage<State> {
        private MockMvc mockMvc;
        /*
        @Hidden
        void create(UuidGenerator uuidGenerator, Repositories repositories) {
            mockMvc = MockMvcBuilders.standaloneSetup(new PersonController(uuidGenerator, repositories.getPersonRepository()))
                    .defaultRequest(get("/").accept(MediaType.APPLICATION_JSON))
                    .alwaysExpect(status().isOk())
                    .alwaysExpect(content().contentType("application/json;charset=UTF-8"))
                    .build();
        }
         */
    }
}
