package io.github.gerardpi.easy.jpaentities.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

public final class PersistableDefsDeserializer {
    private PersistableDefsDeserializer() {
        // No instantation
    }

    public static ObjectMapper createYamlObjectMapper() {
        final YAMLFactory yamlFactory = new YAMLFactory();
        return new ObjectMapper(yamlFactory);
    }

    static EasyJpaEntitiesConfig.Builder slurpFromYaml(final Reader reader, final String yamlFileName, final AnnotationProcessorLogger log) {
        try {
            final EasyJpaEntitiesConfig.Builder builder = createYamlObjectMapper().readValue(reader, EasyJpaEntitiesConfig.Builder.class);
            log.info("Loading persistable defs reading file '" + yamlFileName + "'");
            log.info("It contains " + builder.getEntityClassDefNames().size() + " persistable class defs: " + builder.getEntityClassDefNames());
            return builder;
        } catch (final IOException e) {
            log.error("Error loading persistable defs from file '" + yamlFileName + "': " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    static EntityClassDef.Builder slurpEntityClassDefFromYaml(final Reader reader, final String yamlFileName, final AnnotationProcessorLogger log) {
        try {
            final EntityClassDef.Builder entityClassDef = createYamlObjectMapper().readValue(reader, EntityClassDef.Builder.class);
            log.info("Loaded entity class def reading file '" + yamlFileName + "'");
            return entityClassDef;
        } catch (final IOException e) {
            log.error("Error loading entity class def from file '" + yamlFileName + "': " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }


}
