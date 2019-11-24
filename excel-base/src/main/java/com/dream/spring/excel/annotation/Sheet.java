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

    int headerHeight() default 25;

    int categoryHeight() default 0;

    int contentRowHeight() default 0;

    boolean indexIncluded() default false;

    I18n i18n() default @I18n;

    boolean i18nSupport() default true;

    CellStyle defaultStyle() default @CellStyle(useDefault = true);

    CellStyle defaultHeaderStyle() default @CellStyle(useDefault = true);

    CellStyle defaultCategoryStyle() default @CellStyle(useDefault = true);

    boolean freezeHeader() default true;

    int xOffset() default 0;

    int yOffset() default 0;

    Header[] headers() default {};

    Category[] categories() default {};
}
