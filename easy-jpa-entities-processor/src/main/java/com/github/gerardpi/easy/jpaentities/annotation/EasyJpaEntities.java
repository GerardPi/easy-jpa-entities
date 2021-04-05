package com.github.gerardpi.easy.jpaentities.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface EasyJpaEntities {
    String name() default "";
    String targetPackage() default "";
    boolean slf4jLoggingEnabled() default true;
    boolean includeConstructorWithParameters() default false;
}


