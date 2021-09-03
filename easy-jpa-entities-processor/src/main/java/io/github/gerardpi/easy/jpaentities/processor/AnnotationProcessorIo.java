package io.github.gerardpi.easy.jpaentities.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Optional;

final class AnnotationProcessorIo {
    public static final String LOG_FORMAT_CLASS_AND_MESSAGE = "{}: '{}'";
    private final ProcessingEnvironment processingEnv;
    private final AnnotationProcessorLogger log;

    AnnotationProcessorIo(final ProcessingEnvironment processingEnv, final AnnotationProcessorLogger log) {
        this.processingEnv = processingEnv;
        this.log = log;
    }

    static String getTypeName(final Element e) {
        final TypeMirror typeMirror = e.asType();
        final String[] split = typeMirror.toString().split("\\.");
        return split.length > 0 ? split[split.length - 1] : null;
    }

    static String getPackageName(final TypeElement classElement) {
        return ((PackageElement) classElement.getEnclosingElement()).getQualifiedName().toString();
    }

    AnnotationProcessorLogger getLogger() {
        return log;
    }

    String getQualifiedName(final Element element) {
        if (element instanceof TypeElement) {
            return ((TypeElement) element).getQualifiedName().toString();
        }
        if (element instanceof PackageElement) {
            return ((PackageElement) element).getQualifiedName().toString();
        }
        log.error("Can not get qualified name from", element);
        return null;
    }

    String getPackageName(final Element element) {
        return getQualifiedName(element.getEnclosingElement());
    }

    JavaSourceWriter createJavaSourceWriter(final String fullyQualifiedName) {
        final LineWriter lineWriter = new LineWriter(createSourceWriter(fullyQualifiedName));
        return new JavaSourceWriter(lineWriter);
    }

    LineWriter createLineWriter(final String fullyQualifiedName) {
        return new LineWriter(createSourceWriter(fullyQualifiedName));
    }

    Writer createSourceWriter(final String fullyQualifiedName) {
        try {
            final JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(fullyQualifiedName);
            return sourceFile.openWriter();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    Optional<FileObject> get(final String fullyQualifiedPackagename, final String yamlFilename) {
        try {
            return Optional.of(processingEnv.getFiler().getResource(StandardLocation.CLASS_PATH, fullyQualifiedPackagename, yamlFilename));
        } catch (final IOException e) {
            log.error("Problem fetching resource '" + yamlFilename + "' in '" + fullyQualifiedPackagename + "': " + e.getMessage());
            return Optional.empty();
        }
    }

    JavaSourceGenerator createJavaSourceGenerator() {
        return new JavaSourceGenerator(this, log);
    }
}
