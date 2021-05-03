package io.github.gerardpi.easy.jpaentities.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.gerardpi.easy.jpaentities.processor.ProcessorUtils.error;
import static io.github.gerardpi.easy.jpaentities.processor.ProcessorUtils.note;

public final class PersistableDefsDeserializer {
    private PersistableDefsDeserializer() {
        // No instantation
    }
    public static ObjectMapper createYamlObjectMapper() {
        YAMLFactory yamlFactory = new YAMLFactory();
        return new ObjectMapper(yamlFactory);
    }

    static EasyJpaEntitiesConfig slurpFromYaml(Reader reader, String yamlFileName, ProcessingEnvironment procEnv) {
        try {
            EasyJpaEntitiesConfig easyJpaEntitiesConfig = createYamlObjectMapper().readValue(reader, EasyJpaEntitiesConfig.class);
            note(procEnv, "Loading persistable defs reading file '" + yamlFileName + "'");
            note(procEnv, "It contains " + easyJpaEntitiesConfig.getEntityClassDefNames().size() + " persistable class defs: " + easyJpaEntitiesConfig.getEntityClassDefNames());
            return easyJpaEntitiesConfig;
        } catch (IOException e) {
            error(procEnv, "Error loading persistable defs from file '" + yamlFileName + "': " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    static EntityClassDef slurpEntityClassDefFromYaml(Reader reader, String yamlFileName, ProcessingEnvironment procEnv) {
        try {
            EntityClassDef entityClassDef = createYamlObjectMapper().readValue(reader, EntityClassDef.class);
            note(procEnv, "Loaded entity class def reading file '" + yamlFileName + "'");
            return entityClassDef;
        } catch (IOException e) {
            error(procEnv, "Error loading entity class def from file '" + yamlFileName + "': " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }
}
