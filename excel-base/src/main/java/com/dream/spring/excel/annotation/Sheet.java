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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated at the return model which used to fill the excel row
 *
 * <p>It defines the sheet of excel and its data format and default appearance
 *
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Sheet {

    /**
     * The sheet name in the excel
     *
     * <p>If {@link #i18nSupport() i18nSupport} is true, then the value will be used as the code of i18n
     *
     * @return sheet name
     */
    String value();

    /**
     * @return default width of columns in units of a character width
     */
    int defaultWidth() default 0;

    /**
     * @return Header row height in units of a point.
     */
    int headerHeight() default 0;

    /**
     * @return Category row height in units of a point.
     */
    int categoryHeight() default 0;

    /**
     * @return Content row height in units of a point.
     */
    int contentRowHeight() default 0;

    /**
     * @return If true, the index column will be included as the first column
     */
    boolean indexIncluded() default false;

    /**
     * @return I18n method used for internationalization
     */
    I18n i18n() default @I18n(method = "");

    /**
     * @return If true, the specified {@link Sheet#i18n() i18n method} will be used to internationalize {@link #value() value}
     * @see Sheet#i18n()
     */
    boolean i18nSupport() default true;

    /**
     * @return Default cell style for content cell
     */
    CellStyle defaultStyle() default @CellStyle(useDefault = true);

    /**
     * @return Default header cell style
     */
    CellStyle defaultHeaderStyle() default @CellStyle(useDefault = true);

    /**
     * @return Default category cell style
     */
    CellStyle defaultCategoryStyle() default @CellStyle(useDefault = true);

    /**
     * @return If true, freezes the header row
     */
    boolean[] freezeHeader() default {};

    /**
     * @return Columns offset of the form table
     */
    int xOffset() default 0;

    /**
     * @return Rows offset of the form table
     */
    int yOffset() default 0;

    /**
     * @return Definition of headers
     */
    Header[] headers() default {};

    /**
     * @return Definition of header categories
     */
    Category[] categories() default {};
}
