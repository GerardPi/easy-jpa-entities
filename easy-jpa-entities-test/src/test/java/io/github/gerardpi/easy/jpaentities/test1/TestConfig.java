package io.github.gerardpi.easy.jpaentities.test1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.function.Supplier;

@Configuration
@EnableTransactionManagement
@Transactional
public class TestConfig {
    /**
     * Allow for overriding beans for testing purposes.
     */
    public static final String BEAN_DEF_OVERRIDING_ENABLED = "spring.main.allow-bean-definition-overriding=true";
    private static final Logger LOG = LoggerFactory.getLogger(TestConfig.class);

    public TestConfig() {
        LOG.info("######## {} ##########", TestConfig.class.getSimpleName());
    }

    @Bean
    static UuidGenerator uuidGenerator() {
        return new FixedUuidSeriesGenerator();
    }

    @Bean
    static Supplier<OffsetDateTime> dateTimeSupplier() {
        return new TestDateTimeSupplier();
    }
}
