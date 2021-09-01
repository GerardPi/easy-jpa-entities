package io.github.gerardpi.easy.jpaentities.test1.web;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.BiFunction;

public class ExceptionFactory {
    public static final BiFunction<UUID, Class<?>, NoSuchElementException> ENTITY_NOT_FOUND_BY_ID = (id, entityClass) ->
            new NoSuchElementException("No '" + entityClass.getSimpleName() + "' for ID '" + id + "' exists.");

    private ExceptionFactory() {
        // No instantiation allowed.
    }
}
