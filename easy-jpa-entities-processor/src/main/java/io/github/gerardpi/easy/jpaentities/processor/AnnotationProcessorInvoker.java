package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.annotation.EasyJpaEntities;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes({"io.github.gerardpi.easy.jpaentities.annotation.EasyJpaEntities"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessorInvoker extends AbstractProcessor {

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        final AnnotationProcessorLogger log = new AnnotationProcessorLogger(processingEnv);
        if (annotations.isEmpty()) {
            log.info("Can not find anything annotated with '" + EasyJpaEntities.class.getName() + "'");
        } else {

            final AnnotationProcessor annotationProcessor = new AnnotationProcessor(new AnnotationProcessorIo(processingEnv, log));
            roundEnv.getElementsAnnotatedWith(EasyJpaEntities.class)
                    .forEach(annotationProcessor::processElement);
        }
        return !annotations.isEmpty();
    }
}
