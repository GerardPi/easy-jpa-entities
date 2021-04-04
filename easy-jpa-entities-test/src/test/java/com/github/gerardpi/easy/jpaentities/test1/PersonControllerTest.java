package com.github.gerardpi.easy.jpaentities.test1;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(SpringProfile.TEST)
@SpringBootTest
class PersonControllerTest {
    private static final Logger LOG = LoggerFactory.getLogger(PersonControllerTest.class);

    @Autowired
    private UuidGenerator uuidGenerator;

    @Transactional
    @Test
    public void test() {
    }
}
