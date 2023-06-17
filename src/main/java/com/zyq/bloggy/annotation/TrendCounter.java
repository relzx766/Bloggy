package com.zyq.bloggy.annotation;

import org.intellij.lang.annotations.Language;
import org.springframework.lang.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrendCounter {

    @Language("SpEL") @NonNull String key();
}
