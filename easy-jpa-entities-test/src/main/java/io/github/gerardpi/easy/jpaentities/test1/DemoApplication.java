package io.github.gerardpi.easy.jpaentities.test1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Profile(SpringProfile.PROD)
	@Bean
	UuidGenerator uuidGenerator() {
		return UUID::randomUUID;
	}
}
