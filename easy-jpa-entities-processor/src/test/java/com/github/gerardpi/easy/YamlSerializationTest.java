package com.github.gerardpi.easy;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.gerardpi.easy.jpaentities.processor.PersistableDefsDeserializer;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;

public class YamlSerializationTest {
    @Test
    public void test() {
        EasyJpaEntitiesConfig easyJpaEntitiesConfig = new EasyJpaEntitiesConfig(Arrays.asList("a", "b", "c"));
        try {
            System.out.println(PersistableDefsDeserializer.createYamlObjectMapper().writeValueAsString(easyJpaEntitiesConfig));
        } catch (JsonProcessingException e) {
            fail();
        }
    }
}
