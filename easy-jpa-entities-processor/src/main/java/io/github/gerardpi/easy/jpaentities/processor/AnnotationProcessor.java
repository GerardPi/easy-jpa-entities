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

@SupportedAnnotationTypes({"io.github.gerardpi.easy.jpaentities.annotation.EasyJpaEntities"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations.size() == 0) {
            return false;
        }
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(EasyJpaEntities.class);

        for (Element element : elements) {
            EasyJpaEntities easyJpaEntitiesAnnotation = element.getAnnotation(EasyJpaEntities.class);
            ProcessorUtils.setSlf4jLoggingEnabled(easyJpaEntitiesAnnotation.slf4jLoggingEnabled());
            if (element.getKind().isInterface()) {
                ProcessorUtils.note(processingEnv, "Found annotation " + EasyJpaEntities.class + " on element '" + element + "'");
                String typeName = ProcessorUtils.getTypeName(element);

                String yamlFilename = typeName + ".yaml";
                ProcessorUtils.note(processingEnv, "YAML file to read is = '" + yamlFilename + "'");
                String fullyQualifiedClassname = ProcessorUtils.getQualifiedName(processingEnv, element);
                ProcessorUtils.note(processingEnv, "Fully qualified name='" + fullyQualifiedClassname + "'");
                String fullyQualifiedPackagename = ProcessorUtils.getPackageName(processingEnv, element);
                ProcessorUtils.note(processingEnv, "Fully qualified packagename='" + fullyQualifiedPackagename + "'");
                FileObject yamlFile = ProcessorUtils.get(processingEnv, fullyQualifiedPackagename, yamlFilename)
                        .orElseThrow(() -> new IllegalStateException("Can not fetch resource '" + yamlFilename + "'"));
                EasyJpaEntitiesConfig easyJpaEntitiesConfig = loadPersistableDefNames(yamlFile, element.getEnclosingElement().toString());
                generateClasses(easyJpaEntitiesConfig);
            } else {
                ProcessorUtils.note(processingEnv, "The annotation " + EasyJpaEntities.class + " can only be used on an interface");
            }
        }
        return false;
    }

    private List<EntityClassDef> loadEntityClassDefs(EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        return easyJpaEntitiesConfig.getEntityClassDefNames().stream()
                .map(e -> loadEntityClassDef(e, easyJpaEntitiesConfig.getTargetPackage()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<EntityClassDef> loadEntityClassDef(String entityClassDefName, String targetPackage) {
        String yamlFileName = entityClassDefName + ".yaml";
        FileObject yamlFileObject = ProcessorUtils.get(processingEnv, targetPackage, yamlFileName)
                .orElseThrow(() -> new IllegalStateException("Can not fetch resource '" + yamlFileName + "'"));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(yamlFileObject.openInputStream(), StandardCharsets.UTF_8))) {
            return Optional.of(PersistableDefsDeserializer.slurpEntityClassDefFromYaml(reader, yamlFileObject.getName(), processingEnv)
                    .setName(entityClassDefName)
                    .build());
        } catch (IOException e) {
            ProcessorUtils.error(processingEnv, "Can not read from '" + yamlFileName + "': '" + e.getMessage() + "'");
        }
        return Optional.empty();
    }

    private EasyJpaEntitiesConfig loadPersistableDefNames(FileObject inputYamlFileObject, String defaultTargetPackage) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputYamlFileObject.openInputStream(), StandardCharsets.UTF_8))) {
            EasyJpaEntitiesConfig config = PersistableDefsDeserializer.slurpFromYaml(reader, inputYamlFileObject.getName(), processingEnv)
                    .withDefaultTargetPackageIfNotSpecified(defaultTargetPackage);
            return config.withEntityClassDefs(loadEntityClassDefs(config));
        } catch (IOException e) {
            String msg = "Can not read from '" + inputYamlFileObject.getName() + "': '" + e.getMessage() + "'";
            ProcessorUtils.error(processingEnv, msg);
            throw new IllegalStateException(msg);
        }
    }


    private void generateClasses(EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        ProcessorUtils.note(processingEnv, "Generating mapped superclasses ...");
        generateMappedSuperclasses(easyJpaEntitiesConfig);
        ProcessorUtils.note(processingEnv, "Generating entity classes ...");
        generateEntityClasses(easyJpaEntitiesConfig);
    }

    private void generateEntityClasses(EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        easyJpaEntitiesConfig.getEntityClassDefs().forEach(classDef -> {
            String fqn = easyJpaEntitiesConfig.getTargetPackage() + "." + classDef.getName();
            ProcessorUtils.note(processingEnv, "Generating entity class " + fqn);
            try (JavaSourceWriter writer = ProcessorUtils.createClassWriter(processingEnv, fqn)) {
                new EntityClassGenerator(classDef, easyJpaEntitiesConfig).write(writer);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private void generateMappedSuperclasses(EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        MappedSuperclassGenerator mappedSuperclassGenerator = new MappedSuperclassGenerator(easyJpaEntitiesConfig);

        if (easyJpaEntitiesConfig.hasPersistable()) {
            ProcessorUtils.note(processingEnv, "Generating base class " + MappedSuperclassGenerator.CLASSNAME_PERSISTABLE);
            String fqn = easyJpaEntitiesConfig.getTargetPackage() + "." + MappedSuperclassGenerator.CLASSNAME_PERSISTABLE;
            try (LineWriter writer = ProcessorUtils.createLineWriter(processingEnv, fqn)) {
                mappedSuperclassGenerator.writePersistable(writer);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        if (easyJpaEntitiesConfig.hasOptLockablePersistable()) {
            ProcessorUtils.note(processingEnv, "Generating base class " + MappedSuperclassGenerator.CLASSNAME_OPT_LOCKABLE_PERSISTABLE);
            String fqn = easyJpaEntitiesConfig.getTargetPackage() + "." + MappedSuperclassGenerator.CLASSNAME_OPT_LOCKABLE_PERSISTABLE;
            try (LineWriter writer = ProcessorUtils.createLineWriter(processingEnv, fqn)) {
                mappedSuperclassGenerator.writeOptLockablePersistable(writer);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
