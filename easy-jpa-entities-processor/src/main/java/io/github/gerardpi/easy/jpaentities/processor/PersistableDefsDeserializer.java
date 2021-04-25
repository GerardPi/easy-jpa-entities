package io.github.gerardpi.easy.jpaentities.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;
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
    static PersistableDefs slurpFromYaml(Reader reader, String yamlFileName, ProcessingEnvironment procEnv) {
        YAMLFactory yamlFactory = new YAMLFactory();
        ObjectMapper objectMapper = new ObjectMapper(yamlFactory);
        try {
            PersistableDefs persistableDefs = objectMapper.readValue(reader, PersistableDefs.class);
            note(procEnv, "Loaded persistable defs from file '" + yamlFileName + "'");
            List<String> entityClassNames = persistableDefs.getEntityClassDefs().stream().map(EntityClassDef::getName).collect(Collectors.toList());
            note(procEnv, "It contains " + entityClassNames.size() + " persistable class defs: " + entityClassNames);
            return persistableDefs;
        } catch (IOException e) {
            error(procEnv, "Error loading persistable defs from file '" + yamlFileName + "': " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }
}
