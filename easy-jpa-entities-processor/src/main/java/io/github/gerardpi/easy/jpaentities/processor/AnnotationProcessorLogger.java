package io.github.gerardpi.easy.jpaentities.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.concurrent.atomic.AtomicBoolean;

final class AnnotationProcessorLogger {
    public static final String LOG_FORMAT_CLASS_AND_MESSAGE = "{}: '{}'";
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationProcessorLogger.class);
    private final AtomicBoolean slf4jLoggingEnabled = new AtomicBoolean(false);
    private final ProcessingEnvironment processingEnv;

    AnnotationProcessorLogger(final ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }


    void setSlf4jLoggingEnabled(final boolean enabled) {
        slf4jLoggingEnabled.set(enabled);
    }

    void error(final String msg) {
        if (slf4jLoggingEnabled.get()) {
            LOG.error(LOG_FORMAT_CLASS_AND_MESSAGE, Diagnostic.Kind.ERROR, msg);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg);
    }

    void info(final String msg) {
        if (slf4jLoggingEnabled.get()) {
            LOG.info(LOG_FORMAT_CLASS_AND_MESSAGE, Diagnostic.Kind.NOTE, msg);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }

    void error(final String msg, final Element e) {
        if (slf4jLoggingEnabled.get()) {
            LOG.error(LOG_FORMAT_CLASS_AND_MESSAGE, Diagnostic.Kind.ERROR, msg);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
    }
}
