package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.gerardpi.easy.jpaentities.processor.PersistableDefsDeserializer.slurpEntityClassDefFromYaml;
import static io.github.gerardpi.easy.jpaentities.processor.PersistableDefsDeserializer.slurpFromYaml;
import static io.github.gerardpi.easy.jpaentities.processor.ProcessorUtils.note;

class AnnotationProcessorConfigLoader {
    private final ProcessingEnvironment processingEnv;

    public AnnotationProcessorConfigLoader(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    EasyJpaEntitiesConfig loadConfig(FileObject inputYamlFileObject, String defaultTargetPackage) {
        try {
            EasyJpaEntitiesConfig config = loadConfig(inputYamlFileObject.openInputStream(), inputYamlFileObject.getName(), defaultTargetPackage);
            note(processingEnv, "Loaded " + config.getEntityClassDefs().size() + " instances of " + EntityClassDef.class.getSimpleName());
            return config;
        } catch (IOException | UncheckedIOException e) {
            String msg = "Can not read from '" + inputYamlFileObject.getName() + "': '" + e.getMessage() + "'";
            ProcessorUtils.error(processingEnv, msg);
            throw new IllegalStateException(msg);
        }
    }

    private EasyJpaEntitiesConfig loadConfig(InputStream inputStream, String inputFilename, String defaultTargetPackage) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            EasyJpaEntitiesConfig.Builder builder = slurpFromYaml(reader, inputFilename, processingEnv)
                    .setDefaultIfNoTargetPackageSpecified(defaultTargetPackage);
            List<EntityClassDef> entityClassDefs = loadEntityClassDefs(builder.getEntityClassDefNames(),
                    builder.getTargetPackage(), builder.getDefaultFieldType());
            return builder
                    .setEntityClassDefs(entityClassDefs)
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    private List<EntityClassDef> loadEntityClassDefs(List<String> entityClassDefNames, String targetPackage, String defaultFieldType) {
        return entityClassDefNames.stream()
                .map(e -> loadEntityClassDef(e, targetPackage, defaultFieldType))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<EntityClassDef> loadEntityClassDef(String entityClassDefName, String targetPackage, String defaultFieldType) {
        String yamlFileName = entityClassDefName + ".yaml";
        FileObject yamlFileObject = ProcessorUtils.get(processingEnv, targetPackage, yamlFileName)
                .orElseThrow(() -> new IllegalStateException("Can not fetch resource '" + yamlFileName + "'"));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(yamlFileObject.openInputStream(), StandardCharsets.UTF_8))) {
            EntityClassDef entityClassDef = slurpEntityClassDefFromYaml(reader, yamlFileObject.getName(), processingEnv)
                    .setName(entityClassDefName)
                    .setDefaultFieldTypeIfNotSpecified(defaultFieldType)
                    .build();
            return Optional.of(entityClassDef);
        } catch (IOException e) {
            ProcessorUtils.error(processingEnv, "Can not read from '" + yamlFileName + "': '" + e.getMessage() + "'");
        }
        return Optional.empty();
    }
}
