package com.dream.spring.excel.annotation;

import org.apache.poi.ss.usermodel.CellType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Column {

    CellType type() default CellType.STRING;

    CellStyle style() default @CellStyle(useDefault = true);

    Converter converter() default @Converter;

    boolean i18nSupport() default false;

    CellItemStyle[] cellStyles() default {};
}
