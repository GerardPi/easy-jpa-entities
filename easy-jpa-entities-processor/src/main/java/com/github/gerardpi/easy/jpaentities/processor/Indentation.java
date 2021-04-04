package com.github.gerardpi.easy.jpaentities.processor;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Indentation {
    private final List<String> indentations;
    private final int indentationLevels;
    private final AtomicInteger currentIndentationLevel;

    public Indentation(int indentationLevels, int indentationWidthInSpaces) {
        this.indentationLevels = indentationLevels;
        this.indentations = Collections.unmodifiableList(IntStream.range(0, indentationLevels)
                .mapToObj(indentationLevel -> indentation(indentationLevel, indentationWidthInSpaces))
                .collect(Collectors.toList()));
        this.currentIndentationLevel = new AtomicInteger(0);
    }

    private static String indentation(int indentationLevel, int indentationWidthInSpaces) {
        return String.join("", Collections.nCopies(indentationLevel * indentationWidthInSpaces, " "));
    }

    public void inc() {
        if (currentIndentationLevel.get() < indentationLevels) {
            currentIndentationLevel.incrementAndGet();
        }
    }

    public void dec() {
        if (currentIndentationLevel.get() > 0) {
            currentIndentationLevel.decrementAndGet();
        }
    }

    public String get() {
        return indentations.get(currentIndentationLevel.get());
    }
}
