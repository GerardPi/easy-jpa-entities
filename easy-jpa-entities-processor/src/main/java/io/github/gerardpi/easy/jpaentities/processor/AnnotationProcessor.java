package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.annotation.EasyJpaEntities;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.PersistableDefNames;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.PersistableDefs;

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
import java.util.*;

import static io.github.gerardpi.easy.jpaentities.processor.ProcessorUtils.error;

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

                String yamlFilename = easyJpaEntitiesAnnotation.name().length() > 0
                        ? easyJpaEntitiesAnnotation.name() + ".yaml"
                        : typeName + ".yaml";
                ProcessorUtils.note(processingEnv, "YAML file to read is = '" + yamlFilename + "'");
                String fullyQualifiedClassname = ProcessorUtils.getQualifiedName(processingEnv, element);
                ProcessorUtils.note(processingEnv, "Fully qualified name='" + fullyQualifiedClassname + "'");
                String fullyQualifiedPackagename = ProcessorUtils.getPackageName(processingEnv, element);
                ProcessorUtils.note(processingEnv, "Fully qualified packagename='" + fullyQualifiedPackagename + "'");
                FileObject yamlFile = ProcessorUtils.get(processingEnv, fullyQualifiedPackagename, yamlFilename)
                        .orElseThrow(() -> new IllegalStateException("Can not fetch resource '" + yamlFilename + "'"));
                String targetPackage =
                        easyJpaEntitiesAnnotation.targetPackage().length() > 0
                                ? easyJpaEntitiesAnnotation.targetPackage()
                                : element.getEnclosingElement().toString();
                PersistableDefNames persistableDefNames = loadPersistableDefNames(yamlFile);
                List<EntityClassDef> entityClassDefs = loadEntityClassDefs(persistableDefNames, targetPackage);
                PersistableDefs persistableDefs = new PersistableDefs(UUID.class.getName(), entityClassDefs);
                generateClasses(persistableDefs, targetPackage, easyJpaEntitiesAnnotation.includeConstructorWithParameters());
            } else {
                ProcessorUtils.note(processingEnv, "The annotation " + EasyJpaEntities.class + " can only be used on an interface");
            }
        }
        return false;
    }

    private List<EntityClassDef> loadEntityClassDefs(PersistableDefNames persistableDefNames, String fullyQualifiedPackagename) {
        List<EntityClassDef> entityClassDefs = new ArrayList<>();
        for (String entityClassDefName : persistableDefNames.getEntityClassDefNames()) {
            String yamlFileName = entityClassDefName + ".yaml";
            FileObject yamlFileObject = ProcessorUtils.get(processingEnv, fullyQualifiedPackagename, yamlFileName)
                    .orElseThrow(() -> new IllegalStateException("Can not fetch resource '" + yamlFileName + "'"));
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(yamlFileObject.openInputStream(), StandardCharsets.UTF_8))) {
                entityClassDefs.add(PersistableDefsDeserializer.slurpEntityClassDefFromYaml(reader, yamlFileObject.getName(), processingEnv));
            } catch (IOException e) {
                ProcessorUtils.error(processingEnv, "Can not read from '" + yamlFileName + "': '" + e.getMessage() + "'");
            }
        }
        return Collections.unmodifiableList(entityClassDefs);
    }

    private PersistableDefNames loadPersistableDefNames(FileObject inputYamlFileObject) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputYamlFileObject.openInputStream(), StandardCharsets.UTF_8))) {
            return PersistableDefsDeserializer.slurpFromYaml(reader, inputYamlFileObject.getName(), processingEnv);
        } catch (IOException e) {
            String msg = "Can not read from '" + inputYamlFileObject.getName() + "': '" + e.getMessage() + "'";
            ProcessorUtils.error(processingEnv, msg);
            throw new IllegalStateException(msg);
        }
    }


    private void generateClasses(PersistableDefs persistableDefs, String targetPackage, boolean includeConstructorWithParameters) {
        ProcessorUtils.note(processingEnv, "Generating entity classes ...");
        generateMappedSuperclasses(targetPackage, persistableDefs);
        ProcessorUtils.note(processingEnv, "Generating mapped superclasses ...");
        generateEntityClasses(persistableDefs, targetPackage, includeConstructorWithParameters);
    }

    private void generateEntityClasses(PersistableDefs persistableDefs, String targetPackage, boolean includeConstructorWithParameters) {
        persistableDefs.getEntityClassDefs().forEach(classDef -> {
            String fqn = targetPackage + "." + classDef.getName();
            ProcessorUtils.note(processingEnv, "Generating entity class " + fqn);
            try (JavaSourceWriter writer = ProcessorUtils.createClassWriter(processingEnv, fqn)) {
                new EntityClassGenerator(classDef, targetPackage, includeConstructorWithParameters, persistableDefs.getIdClass()).write(writer);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private void generateMappedSuperclasses(String targetPackage, PersistableDefs persistableDefs) {
        MappedSuperclassGenerator mappedSuperclassGenerator = new MappedSuperclassGenerator(targetPackage);

        if (persistableDefs.hasPersistable()) {
            ProcessorUtils.note(processingEnv, "Generating base class " + MappedSuperclassGenerator.CLASSNAME_PERSISTABLE);
            String fqn = targetPackage + "." + MappedSuperclassGenerator.CLASSNAME_PERSISTABLE;
            try (LineWriter writer = ProcessorUtils.createLineWriter(processingEnv, fqn)) {
                mappedSuperclassGenerator.writePersistable(writer, persistableDefs);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        if (persistableDefs.hasOptLockablePersistable()) {
            ProcessorUtils.note(processingEnv, "Generating base class " + MappedSuperclassGenerator.CLASSNAME_OPT_LOCKABLE_PERSISTABLE);
            String fqn = targetPackage + "." + MappedSuperclassGenerator.CLASSNAME_OPT_LOCKABLE_PERSISTABLE;
            try (LineWriter writer = ProcessorUtils.createLineWriter(processingEnv, fqn)) {
                mappedSuperclassGenerator.writeOptLockablePersistable(writer, persistableDefs);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
