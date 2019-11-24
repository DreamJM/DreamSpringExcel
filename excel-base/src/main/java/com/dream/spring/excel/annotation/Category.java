package com.dream.spring.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface Category {

    String value();

    int start();

    int end();

    boolean i18nSupport() default true;

    CellStyle style() default @CellStyle(useDefault = true);
}
