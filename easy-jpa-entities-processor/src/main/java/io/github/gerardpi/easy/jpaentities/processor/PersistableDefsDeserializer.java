package io.github.gerardpi.easy.jpaentities.processor;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

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

    static EasyJpaEntitiesConfig.Builder slurpFromYaml(Reader reader, String yamlFileName, ProcessingEnvironment procEnv) {
        try {
            EasyJpaEntitiesConfig.Builder builder = createYamlObjectMapper().readValue(reader, EasyJpaEntitiesConfig.Builder.class);
            note(procEnv, "Loading persistable defs reading file '" + yamlFileName + "'");
            note(procEnv, "It contains " + builder.getEntityClassDefNames().size() + " persistable class defs: " + builder.getEntityClassDefNames());
            return builder;
        } catch (IOException e) {
            error(procEnv, "Error loading persistable defs from file '" + yamlFileName + "': " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    static EntityClassDef.Builder slurpEntityClassDefFromYaml(Reader reader, String yamlFileName, ProcessingEnvironment procEnv, String defaultFieldType) {
        try {
            ObjectMapper yamlObjectMapper = createYamlObjectMapper();
            InjectableValues injectableValues = new InjectableValues.Std().addValue(String.class, defaultFieldType);
            yamlObjectMapper.setInjectableValues(injectableValues);
            EntityClassDef.Builder entityClassDef = yamlObjectMapper.readValue(reader, EntityClassDef.Builder.class);
            note(procEnv, "Loaded entity class def reading file '" + yamlFileName + "'");
            return entityClassDef;
        } catch (IOException e) {
            error(procEnv, "Error loading entity class def from file '" + yamlFileName + "': " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }


}
