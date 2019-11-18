package com.dream.spring.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface AnnotationMember {

    String name();

    String value();

    Class<?> annotation() default AnnotationIgnore.class;
}
