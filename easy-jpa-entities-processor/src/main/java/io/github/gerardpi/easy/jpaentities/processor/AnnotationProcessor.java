package io.github.gerardpi.easy.jpaentities.processor;

import com.google.common.base.Preconditions;
import io.github.gerardpi.easy.jpaentities.annotation.EasyJpaEntities;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.tools.FileObject;

@SupportedAnnotationTypes({"io.github.gerardpi.easy.jpaentities.annotation.EasyJpaEntities"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor {
    private final AnnotationProcessorIo io;
    private final AnnotationProcessorLogger log;

    public AnnotationProcessor(final AnnotationProcessorIo io) {
        this.io = io;
        this.log = io.getLogger();
    }

    void processElement(final Element element) {
        final EasyJpaEntities easyJpaEntitiesAnnotation = element.getAnnotation(EasyJpaEntities.class);
        Preconditions.checkArgument(easyJpaEntitiesAnnotation != null, "No annotation of type '" + EasyJpaEntities.class.getName() + "' was found.");
        log.setSlf4jLoggingEnabled(easyJpaEntitiesAnnotation.slf4jLoggingEnabled());
        if (element.getKind().isInterface()) {
            log.info("Found annotation " + EasyJpaEntities.class + " on element '" + element + "'");

            final EasyJpaEntitiesConfig easyJpaEntitiesConfig = new AnnotationProcessorConfigLoader(io, log)
                    .loadConfig(createConfigFileObject(element), element.getEnclosingElement().toString());
            generateClasses(easyJpaEntitiesConfig);
        } else {
            log.info("The annotation " + EasyJpaEntities.class + " can only be used on an interface");
        }
    }

    private FileObject createConfigFileObject(final Element element) {
        final String typeName = AnnotationProcessorIo.getTypeName(element);
        final String fullyQualifiedPackagename = io.getPackageName(element);
        log.info("Fully qualified packagename='" + fullyQualifiedPackagename + "'");
        final String yamlFilename = typeName + ".yaml";
        log.info("YAML file to read is = '" + yamlFilename + "'");
        return io.get(fullyQualifiedPackagename, yamlFilename)
                .orElseThrow(() -> new IllegalStateException("Can not fetch resource '" + yamlFilename + "'"));
    }

    private void generateClasses(final EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        log.info("Generating mapped superclasses ...");
        final JavaSourceGenerator sourceGenerator = io.createJavaSourceGenerator();
        sourceGenerator.generateMappedSuperclasses(easyJpaEntitiesConfig);
        log.info("Generating entity classes ...");
        sourceGenerator.generateEntityClasses(easyJpaEntitiesConfig);
        sourceGenerator.generateDtoClasses(easyJpaEntitiesConfig);
    }
}
