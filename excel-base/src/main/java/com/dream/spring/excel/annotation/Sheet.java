package com.dream.spring.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Sheet {

    String value();

    int defaultWidth() default 12;

    int defaultHeaderHeight() default 25;

    int contentRowHeight() default 0;

    boolean indexIncluded() default false;

    I18n i18n() default @I18n;

    boolean i18nSupport() default true;

    Header[] headers() default {};

    Category[] categories() default {};
}
