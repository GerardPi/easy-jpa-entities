package io.github.gerardpi.easy.jpaentities.test1.web;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.BiFunction;

public class ExceptionFactory {
    private static final String CAN_NOT_FIND_AN = "Can not find an ";
    public static final BiFunction<UUID, Class<?>, NoSuchElementException> ENTITY_NOT_FOUND_BY_ID = (id, entityClass) ->
            new NoSuchElementException(CAN_NOT_FIND_AN + entityClass.getSimpleName() + " for ID " + id);

    private ExceptionFactory() {
        // No instantiation allowed.
    }
}
