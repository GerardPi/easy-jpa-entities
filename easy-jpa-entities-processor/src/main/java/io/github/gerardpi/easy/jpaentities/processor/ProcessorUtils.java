package io.github.gerardpi.easy.jpaentities.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

final class ProcessorUtils {
    private static final AtomicBoolean slf4jLoggingEnabled = new AtomicBoolean(false);
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ProcessorUtils.class);
    public static final String LOG_FORMAT_CLASS_AND_MESSAGE = "{}: '{}'";

    private ProcessorUtils() {
        // No instantiation
    }

    static void setSlf4jLoggingEnabled(boolean enabled) {
        slf4jLoggingEnabled.set(enabled);
    }

    static String getTypeName(Element e) {
        TypeMirror typeMirror = e.asType();
        String[] split = typeMirror.toString().split("\\.");
        return split.length > 0 ? split[split.length - 1] : null;
    }

    static String getPackageName(TypeElement classElement) {
        return ((PackageElement) classElement.getEnclosingElement()).getQualifiedName().toString();
    }

    static String getQualifiedName(ProcessingEnvironment processingEnv, Element element) {
        if (element instanceof TypeElement) {
            return ((TypeElement) element).getQualifiedName().toString();
        }
        if (element instanceof PackageElement) {
            return ((PackageElement) element).getQualifiedName().toString();
        }
        ProcessorUtils.error(processingEnv, "Can not get qualified name from", element);
        return null;
    }

    static String getPackageName(ProcessingEnvironment processingEnv, Element element) {
        return getQualifiedName(processingEnv, element.getEnclosingElement());
    }

    static void error(ProcessingEnvironment processingEnv, String msg, Element e) {
        if (slf4jLoggingEnabled.get()) {
            LOG.error(LOG_FORMAT_CLASS_AND_MESSAGE, Diagnostic.Kind.ERROR, msg);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
    }

    static void error(ProcessingEnvironment processingEnv, String msg) {
        if (slf4jLoggingEnabled.get()) {
            LOG.error(LOG_FORMAT_CLASS_AND_MESSAGE, Diagnostic.Kind.ERROR, msg);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg);
    }

    static void note(ProcessingEnvironment processingEnv, String msg) {
        if (slf4jLoggingEnabled.get()) {
            LOG.info(LOG_FORMAT_CLASS_AND_MESSAGE, Diagnostic.Kind.NOTE, msg);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }

    static JavaSourceWriter createJavaSourceWriter(ProcessingEnvironment processingEnv, String fullyQualifiedName) {
        LineWriter lineWriter = new LineWriter(createSourceWriter(processingEnv, fullyQualifiedName));
        return new JavaSourceWriter(lineWriter);
    }

    static LineWriter createLineWriter(ProcessingEnvironment processingEnv, String fullyQualifiedName) {
        return new LineWriter(createSourceWriter(processingEnv, fullyQualifiedName));
    }

    static Writer createSourceWriter(ProcessingEnvironment processingEnv, String fullyQualifiedName) {
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(fullyQualifiedName);
            return sourceFile.openWriter();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static Optional<FileObject> get(ProcessingEnvironment processingEnv, String fullyQualifiedPackagename, String yamlFilename) {
        try {
            return Optional.of(processingEnv.getFiler().getResource(StandardLocation.CLASS_PATH, fullyQualifiedPackagename, yamlFilename));
        } catch (IOException e) {
            error(processingEnv, "Problem fetching resource '" + yamlFilename + "' in '" + fullyQualifiedPackagename + "': " + e.getMessage());
            return Optional.empty();
        }
    }
}
