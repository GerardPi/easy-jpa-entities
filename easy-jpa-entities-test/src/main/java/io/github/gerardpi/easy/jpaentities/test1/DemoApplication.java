package io.github.gerardpi.easy.jpaentities.test1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@SpringBootApplication
public class DemoApplication {
    private static final Logger LOG = LoggerFactory.getLogger(DemoApplication.class);

	public DemoApplication() {
		LOG.info("################## {}", DemoApplication.class.getName());
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
    }

    @Profile(SpringProfile.PROD)
    @Bean
    UuidGenerator uuidGenerator() {
        return UUID::randomUUID;
    }
}
