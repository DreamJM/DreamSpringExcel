package com.dream.spring.excel.annotation;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface CellStyle {

    IndexedColors backgroundColor() default IndexedColors.WHITE;

    IndexedColors fontColor() default IndexedColors.BLACK;

    String fontName() default "";

    int fontSize() default 0;

    HorizontalAlignment horizontalAlignment() default HorizontalAlignment.CENTER;

    VerticalAlignment verticalAlignment() default VerticalAlignment.CENTER;

    boolean useDefault() default false;

}
