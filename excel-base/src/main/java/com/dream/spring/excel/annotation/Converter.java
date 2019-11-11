package com.dream.spring.excel.annotation;

import com.dream.spring.excel.StringUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface Converter {

    Class<?> clazz() default StringUtils.class;

    String method() default "valueOf";

}
