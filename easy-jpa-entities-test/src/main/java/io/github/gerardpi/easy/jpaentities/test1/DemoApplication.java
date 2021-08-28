package io.github.gerardpi.easy.jpaentities.test1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gerardpi.easy.jpaentities.test1.json.ObjectMapperHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DemoApplication implements WebMvcConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(DemoApplication.class);

    public DemoApplication() {
        LOG.info("######### DEMO #########");
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return ObjectMapperHolder.getIntance().getObjectMapper();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                .map(c -> (MappingJackson2HttpMessageConverter) c)
                .forEach(c -> c.setObjectMapper(ObjectMapperHolder.getIntance().getObjectMapper()));
        LoggerFactory.getLogger(ObjectMapperHolder.class).info("Setting an initial objectMapper");
    }

    @Bean
    UuidGenerator uuidGenerator() {
        return UUID::randomUUID;
    }
}
