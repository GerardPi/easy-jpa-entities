package io.github.gerardpi.easy.jpaentities.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.PersistableDefNames;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.PersistableDefs;

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
    static PersistableDefs slurpFromYamlOrig(Reader reader, String yamlFileName, ProcessingEnvironment procEnv) {
        try {
            PersistableDefs persistableDefs = createYamlObjectMapper().readValue(reader, PersistableDefs.class);
            note(procEnv, "Loaded persistable defs from file '" + yamlFileName + "'");
            List<String> entityClassNames = persistableDefs.getEntityClassDefs().stream()
                    .map(EntityClassDef::getName)
                    .collect(Collectors.toList());
            note(procEnv, "It contains " + entityClassNames.size() + " persistable class defs: " + entityClassNames);
            return persistableDefs;
        } catch (IOException e) {
            error(procEnv, "Error loading persistable defs from file '" + yamlFileName + "': " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    static PersistableDefNames slurpFromYaml(Reader reader, String yamlFileName, ProcessingEnvironment procEnv) {
        try {
            PersistableDefNames persistableDefNames = createYamlObjectMapper().readValue(reader, PersistableDefNames.class);
            note(procEnv, "Loading persistable defs reading file '" + yamlFileName + "'");
            note(procEnv, "It contains " + persistableDefNames.getEntityClassDefNames().size() + " persistable class defs: " + persistableDefNames.getEntityClassDefNames());
            return persistableDefNames;
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
