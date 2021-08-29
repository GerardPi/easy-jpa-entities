package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.annotation.EasyJpaEntities;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.gerardpi.easy.jpaentities.processor.PersistableDefsDeserializer.slurpEntityClassDefFromYaml;
import static io.github.gerardpi.easy.jpaentities.processor.PersistableDefsDeserializer.slurpFromYaml;
import static io.github.gerardpi.easy.jpaentities.processor.ProcessorUtils.*;

@SupportedAnnotationTypes({"io.github.gerardpi.easy.jpaentities.annotation.EasyJpaEntities"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations.isEmpty()) {
            return false;
        }
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(EasyJpaEntities.class);

        for (Element element : elements) {
            EasyJpaEntities easyJpaEntitiesAnnotation = element.getAnnotation(EasyJpaEntities.class);
            setSlf4jLoggingEnabled(easyJpaEntitiesAnnotation.slf4jLoggingEnabled());
            if (element.getKind().isInterface()) {
                note(processingEnv, "Found annotation " + EasyJpaEntities.class + " on element '" + element + "'");

                EasyJpaEntitiesConfig easyJpaEntitiesConfig = loadConfig(createConfigFileObject(element), element.getEnclosingElement().toString());
                generateClasses(easyJpaEntitiesConfig);
            } else {
                note(processingEnv, "The annotation " + EasyJpaEntities.class + " can only be used on an interface");
            }
        }
        return true;
    }

    private FileObject createConfigFileObject(Element element) {
        String typeName = ProcessorUtils.getTypeName(element);
        String fullyQualifiedPackagename = getPackageName(processingEnv, element);
        note(processingEnv, "Fully qualified packagename='" + fullyQualifiedPackagename + "'");
        String yamlFilename = typeName + ".yaml";
        note(processingEnv, "YAML file to read is = '" + yamlFilename + "'");
        return ProcessorUtils.get(processingEnv, fullyQualifiedPackagename, yamlFilename)
                .orElseThrow(() -> new IllegalStateException("Can not fetch resource '" + yamlFilename + "'"));
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

    private EasyJpaEntitiesConfig loadConfig(FileObject inputYamlFileObject, String defaultTargetPackage) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputYamlFileObject.openInputStream(), StandardCharsets.UTF_8))) {
            EasyJpaEntitiesConfig.Builder builder = slurpFromYaml(reader, inputYamlFileObject.getName(), processingEnv)
                    .setDefaultIfNoTargetPackageSpecified(defaultTargetPackage);
            List<EntityClassDef> entityClassDefs = loadEntityClassDefs(builder.getEntityClassDefNames(), builder.getTargetPackage(), builder.getDefaultFieldType());
            note(processingEnv, "Loaded " + entityClassDefs.size() + " instances of " + EntityClassDef.class.getSimpleName());
            return builder
                    .setEntityClassDefs(entityClassDefs)
                    .build();
        } catch (IOException e) {
            String msg = "Can not read from '" + inputYamlFileObject.getName() + "': '" + e.getMessage() + "'";
            ProcessorUtils.error(processingEnv, msg);
            throw new IllegalStateException(msg);
        }
    }


    private void generateClasses(EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        note(processingEnv, "Generating mapped superclasses ...");
        generateMappedSuperclasses(easyJpaEntitiesConfig);
        note(processingEnv, "Generating entity classes ...");
        generateEntityClasses(easyJpaEntitiesConfig);
        generateDtoClasses(easyJpaEntitiesConfig);
    }

    private void generateEntityClasses(EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        easyJpaEntitiesConfig.getEntityClassDefs().forEach(classDef -> {
            String fqn = easyJpaEntitiesConfig.getTargetPackage() + "." + classDef.getName();
            note(processingEnv, "Generating entity class " + fqn);
            try (JavaSourceWriter writer = createJavaSourceWriter(processingEnv, fqn)) {
                new EntityClassGenerator(classDef, easyJpaEntitiesConfig)
                        .write(writer);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private void generateDtoClasses(EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        easyJpaEntitiesConfig.getEntityClassDefs().forEach(classDef -> {
            if (classDef.getDtoTargetPackage().isPresent()) {
                String fqn = classDef.getDtoTargetPackage().get() + "." + classDef.getName() + "Dto";
                note(processingEnv, "Generating DTO class " + fqn);
                try (JavaSourceWriter writer = createJavaSourceWriter(processingEnv, fqn)) {
                    new EntityClassGenerator(classDef, easyJpaEntitiesConfig, true)
                            .write(writer);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            } else {
                note(processingEnv, "No DTO class generated for '" + classDef.getName() + "'");
            }
        });
    }

    private void generateMappedSuperclasses(EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        SuperclassGenerator superClassGenerator = new SuperclassGenerator(easyJpaEntitiesConfig);

        writeSuperclass(easyJpaEntitiesConfig, EntityClassDef.CLASSNAME_PERSISTABLE_ENTITY, superClassGenerator);
        writeSuperclass(easyJpaEntitiesConfig, EntityClassDef.CLASSNAME_PERSISTABLE_ENTITY_WITH_TAG, superClassGenerator);
        writeSuperclass(easyJpaEntitiesConfig, EntityClassDef.CLASSNAME_ENTITY_DTO, superClassGenerator);
        writeSuperclass(easyJpaEntitiesConfig, EntityClassDef.CLASSNAME_ENTITY_DTO_WITH_TAG, superClassGenerator);
    }

    private void writeSuperclass(EasyJpaEntitiesConfig config, String superClassName, SuperclassGenerator superclassGenerator) {
        note(processingEnv, "Generating base class " + superClassName);
        String fqn = config.getTargetPackage() + "." + superClassName;
        try (LineWriter writer = ProcessorUtils.createLineWriter(processingEnv, fqn)) {
            superclassGenerator.write(superClassName, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
