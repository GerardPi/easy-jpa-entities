package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;

import javax.tools.FileObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.gerardpi.easy.jpaentities.processor.PersistableDefsDeserializer.slurpEntityClassDefFromYaml;
import static io.github.gerardpi.easy.jpaentities.processor.PersistableDefsDeserializer.slurpFromYaml;

class AnnotationProcessorConfigLoader {
    private final AnnotationProcessorIo io;
    private final AnnotationProcessorLogger log;

    public AnnotationProcessorConfigLoader(final AnnotationProcessorIo io, final AnnotationProcessorLogger log) {
        this.io = io;
        this.log = log;
    }

    EasyJpaEntitiesConfig loadConfig(final FileObject inputYamlFileObject, final String defaultTargetPackage) {
        try {
            final EasyJpaEntitiesConfig config = loadConfig(inputYamlFileObject.openInputStream(), inputYamlFileObject.getName(), defaultTargetPackage);
            log.info("Loaded " + config.getEntityClassDefs().size() + " instances of " + EntityClassDef.class.getSimpleName());
            return config;
        } catch (final IOException | UncheckedIOException e) {
            final String msg = "Can not read from '" + inputYamlFileObject.getName() + "': '" + e.getMessage() + "'";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
    }

    private EasyJpaEntitiesConfig loadConfig(final InputStream inputStream, final String inputFilename, final String defaultTargetPackage) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            final EasyJpaEntitiesConfig.Builder builder = slurpFromYaml(reader, inputFilename, log)
                    .setDefaultIfNoTargetPackageSpecified(defaultTargetPackage);
            final List<EntityClassDef> entityClassDefs = loadEntityClassDefs(builder.getEntityClassDefNames(),
                    builder.getTargetPackage(), builder.getDefaultFieldType());
            return builder
                    .setEntityClassDefs(entityClassDefs)
                    .build();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<EntityClassDef> loadEntityClassDefs(final List<String> entityClassDefNames, final String targetPackage, final String defaultFieldType) {
        return entityClassDefNames.stream()
                .map(e -> loadEntityClassDef(e, targetPackage, defaultFieldType))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<EntityClassDef> loadEntityClassDef(final String entityClassDefName, final String targetPackage, final String defaultFieldType) {
        final String yamlFileName = entityClassDefName + ".yaml";
        final FileObject yamlFileObject = io.get(targetPackage, yamlFileName)
                .orElseThrow(() -> new IllegalStateException("Can not fetch resource '" + yamlFileName + "'"));
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(yamlFileObject.openInputStream(), StandardCharsets.UTF_8))) {
            final EntityClassDef entityClassDef = slurpEntityClassDefFromYaml(reader, yamlFileObject.getName(), log)
                    .setName(entityClassDefName)
                    .setDefaultFieldTypeIfNotSpecified(defaultFieldType)
                    .build();
            return Optional.of(entityClassDef);
        } catch (final IOException e) {
            log.error("Can not read from '" + yamlFileName + "': '" + e.getMessage() + "'");
        }
        return Optional.empty();
    }
}
