package io.github.gerardpi.easy.jpaentities.test1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableTransactionManagement
@Transactional
public class TestConfig {
    private static final Logger LOG = LoggerFactory.getLogger(TestConfig.class);

    @Profile(SpringProfile.TEST)
    @Bean
    UuidGenerator uuidGenerator() {
        return new FixedUuidSeriesGenerator();
    }
}
