package io.github.gerardpi.easy;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.gerardpi.easy.jpaentities.processor.PersistableDefsDeserializer;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;

class YamlSerializationTest {
    @Test
    void test() {
        final EasyJpaEntitiesConfig easyJpaEntitiesConfig = new EasyJpaEntitiesConfig.Builder(
                "targetPackage", false, Arrays.asList("a", "b", "c"),
                UUID.class.getName(), true,
                false, null, "commonPackage")
                .build();
        try {
            System.out.println(PersistableDefsDeserializer.createYamlObjectMapper().writeValueAsString(easyJpaEntitiesConfig));
        } catch (final JsonProcessingException e) {
            fail();
        }
    }
}
