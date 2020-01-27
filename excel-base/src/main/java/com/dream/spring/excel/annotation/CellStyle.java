/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dream.spring.excel.annotation;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents the cell style(eg: color, font etc.)
 *
 * @author DreamJM
 * @see CellItemStyle
 * @see Column
 * @see Category
 * @see Header
 * @see Sheet
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface CellStyle {

    /**
     * @return Background color of excel cell
     */
    IndexedColors backgroundColor() default IndexedColors.WHITE;

    /**
     * @return Font color of excel cell
     */
    IndexedColors fontColor() default IndexedColors.BLACK;

    /**
     * @return Font family name of excel cell
     */
    String fontName() default "";

    /**
     * @return Font size of excel cell (number &lt;= 0 will be ignored)
     */
    int fontSize() default 0;

    /**
     * @return {@link HorizontalAlignment Horizontal alignment} of excel cell
     * @see HorizontalAlignment
     */
    HorizontalAlignment horizontalAlignment() default HorizontalAlignment.CENTER;

    /**
     * @return {@link VerticalAlignment Vertical alignment} of excel cell
     * @see VerticalAlignment
     */
    VerticalAlignment verticalAlignment() default VerticalAlignment.CENTER;

    /**
     * @return If true, the default style will be used and above style parameters will be ignored
     */
    boolean useDefault() default false;

}
