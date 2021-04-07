package com.github.gerardpi.easy.jpaentities.processor;

import com.github.gerardpi.easy.jpaentities.annotation.EasyJpaEntities;
import com.github.gerardpi.easy.jpaentities.processor.entitydefs.PersistableDefs;

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
import java.util.Set;

import static com.github.gerardpi.easy.jpaentities.processor.ProcessorUtils.error;
import static com.github.gerardpi.easy.jpaentities.processor.ProcessorUtils.note;

@SupportedAnnotationTypes({"com.github.gerardpi.easy.jpaentities.annotation.EasyJpaEntities"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        note(processingEnv, "Started " + getClass().getSimpleName());
        if (annotations.size() == 0) {
            return false;
        }
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(EasyJpaEntities.class);

        for (Element element : elements) {
            EasyJpaEntities easyJpaEntitiesAnnotation = element.getAnnotation(EasyJpaEntities.class);
            ProcessorUtils.setSlf4jLoggingEnabled(easyJpaEntitiesAnnotation.slf4jLoggingEnabled());
            if (element.getKind().isInterface()) {
                note(processingEnv, "Found annotation " + EasyJpaEntities.class + " on element '" + element + "'");
                String typeName = ProcessorUtils.getTypeName(element);

                String yamlFilename = easyJpaEntitiesAnnotation.name().length() > 0
                        ? easyJpaEntitiesAnnotation.name() + ".yaml"
                        : typeName + ".yaml";
                note(processingEnv, "YAML file to read is = '" + yamlFilename + "'");
                String fullyQualifiedClassname = ProcessorUtils.getQualifiedName(processingEnv, element);
                note(processingEnv, "Fully qualified name='" + fullyQualifiedClassname + "'");
                String fullyQualifiedPackagename = ProcessorUtils.getPackageName(processingEnv, element);
                note(processingEnv, "Fully qualified packagename='" + fullyQualifiedPackagename + "'");
                FileObject yamlFile = ProcessorUtils.get(processingEnv, fullyQualifiedPackagename, yamlFilename)
                        .orElseThrow(() -> new IllegalStateException("Can not fetch resource '" + yamlFilename + "'"));
                String targetPackage =
                        easyJpaEntitiesAnnotation.targetPackage().length() > 0
                                ? easyJpaEntitiesAnnotation.targetPackage()
                                : element.getEnclosingElement().toString();
                generate(yamlFile, targetPackage, easyJpaEntitiesAnnotation.includeConstructorWithParameters());
            } else {
                note(processingEnv, "The annotation " + EasyJpaEntities.class + " can only be used on an interface");
            }
        }
        return false;
    }

    private void generate(FileObject inputYamlFileObject, String targetPackage, boolean includeConstructorWithParameters) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputYamlFileObject.openInputStream(), StandardCharsets.UTF_8))) {
            PersistableDefs persistableDefs = PersistableDefsDeserializer.slurpFromYaml(reader, inputYamlFileObject.getName(), processingEnv);
            generateClasses(persistableDefs, targetPackage, includeConstructorWithParameters);
        } catch (IOException e) {
            error(processingEnv, "Can not read from '" + inputYamlFileObject.getName() + "': '" + e.getMessage() + "'");
        }
    }


    private void generateClasses(PersistableDefs persistableDefs, String targetPackage, boolean includeConstructorWithParameters) {
        generateMappedSuperclasses(targetPackage, persistableDefs);
        generateEntityClasses(persistableDefs, targetPackage, includeConstructorWithParameters);
    }

    private void generateEntityClasses(PersistableDefs persistableDefs, String targetPackage, boolean includeConstructorWithParameters) {
        note(processingEnv, "Generating entity classes...");
        persistableDefs.getEntityClassDefs().forEach(classDef -> {
            String entityFqn = targetPackage + "." + classDef.getName();
            try (JavaSourceWriter writer = ProcessorUtils.createClassWriter(processingEnv, entityFqn)) {
                new EntityClassGenerator(classDef, targetPackage, includeConstructorWithParameters, persistableDefs.getIdClass()).write(writer);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private void generateMappedSuperclasses(String targetPackage, PersistableDefs persistableDefs) {
        MappedSuperclassGenerator mappedSuperclassGenerator = new MappedSuperclassGenerator(targetPackage);

        if (persistableDefs.isWritePersistable()) {
            note(processingEnv, "Generating base class " + MappedSuperclassGenerator.CLASSNAME_PERSISTABLE + "...");
            String persistableFqn = targetPackage + "." + MappedSuperclassGenerator.CLASSNAME_PERSISTABLE;
            try (LineWriter writer = ProcessorUtils.createLineWriter(processingEnv, persistableFqn)) {
                mappedSuperclassGenerator.writePersistable(writer, persistableDefs);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        if (persistableDefs.isWriteRewritablePersistable()) {
            note(processingEnv, "Generating base class " + MappedSuperclassGenerator.CLASSNAME_REWRITABLE_PERSISTABLE + "...");
            String rewritablePersistableFqn = targetPackage + "." + MappedSuperclassGenerator.CLASSNAME_REWRITABLE_PERSISTABLE;
            try (LineWriter writer = ProcessorUtils.createLineWriter(processingEnv, rewritablePersistableFqn)) {
                mappedSuperclassGenerator.writeRewritablePersistable(writer, persistableDefs);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
