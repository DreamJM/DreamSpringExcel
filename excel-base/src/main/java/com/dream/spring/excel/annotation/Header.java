package com.dream.spring.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface Header {

    String value();

    String field() default "";

    int width() default 12;

    CellStyle style() default @CellStyle;

    boolean i18nSupport() default true;
}
