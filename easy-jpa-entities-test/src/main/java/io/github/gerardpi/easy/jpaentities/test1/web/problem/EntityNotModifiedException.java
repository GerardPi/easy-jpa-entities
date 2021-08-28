package io.github.gerardpi.easy.jpaentities.test1.web.problem;

public class EntityNotModifiedException extends IllegalArgumentException {
    public EntityNotModifiedException(String message) {
        super(message);
    }
}
