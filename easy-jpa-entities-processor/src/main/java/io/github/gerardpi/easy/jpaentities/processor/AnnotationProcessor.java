package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.annotation.EasyJpaEntities;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.tools.FileObject;

import static io.github.gerardpi.easy.jpaentities.processor.ProcessorUtils.*;

@SupportedAnnotationTypes({"io.github.gerardpi.easy.jpaentities.annotation.EasyJpaEntities"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor {
    private final ProcessingEnvironment processingEnv;

    public AnnotationProcessor(final ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    void processElement(final Element element) {
        final EasyJpaEntities easyJpaEntitiesAnnotation = element.getAnnotation(EasyJpaEntities.class);
        setSlf4jLoggingEnabled(easyJpaEntitiesAnnotation.slf4jLoggingEnabled());
        if (element.getKind().isInterface()) {
            note(processingEnv, "Found annotation " + EasyJpaEntities.class + " on element '" + element + "'");

            final EasyJpaEntitiesConfig easyJpaEntitiesConfig = new AnnotationProcessorConfigLoader(processingEnv)
                    .loadConfig(createConfigFileObject(element), element.getEnclosingElement().toString());
            generateClasses(easyJpaEntitiesConfig);
        } else {
            note(processingEnv, "The annotation " + EasyJpaEntities.class + " can only be used on an interface");
        }
    }

    private FileObject createConfigFileObject(final Element element) {
        final String typeName = ProcessorUtils.getTypeName(element);
        final String fullyQualifiedPackagename = getPackageName(processingEnv, element);
        note(processingEnv, "Fully qualified packagename='" + fullyQualifiedPackagename + "'");
        final String yamlFilename = typeName + ".yaml";
        note(processingEnv, "YAML file to read is = '" + yamlFilename + "'");
        return ProcessorUtils.get(processingEnv, fullyQualifiedPackagename, yamlFilename)
                .orElseThrow(() -> new IllegalStateException("Can not fetch resource '" + yamlFilename + "'"));
    }

    private void generateClasses(final EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        note(processingEnv, "Generating mapped superclasses ...");
        final JavaSourceGenerator sourceGenerator = new JavaSourceGenerator(processingEnv);
        sourceGenerator.generateMappedSuperclasses(easyJpaEntitiesConfig);
        note(processingEnv, "Generating entity classes ...");
        sourceGenerator.generateEntityClasses(easyJpaEntitiesConfig);
        sourceGenerator.generateDtoClasses(easyJpaEntitiesConfig);
    }
}
