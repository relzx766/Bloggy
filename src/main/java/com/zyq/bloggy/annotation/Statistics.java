package com.zyq.bloggy.annotation;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.intellij.lang.annotations.Language;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Statistics {
    String cacheName() default "";

    @Language("SpEL") @NotNull String key();

    int delta() default 1;

    TimeUnit util() default TimeUnit.DAYS;


    long timeout() default -1;

}
