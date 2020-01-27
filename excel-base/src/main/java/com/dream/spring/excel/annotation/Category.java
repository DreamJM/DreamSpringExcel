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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents the header category should be added above the form header.
 *
 * <p>Examples:
 * <pre>
 * -----------------------------------------
 *          |     category1     | category2
 *  header1  -------------------------------
 *          | header2 | header3 | header4
 * -----------------------------------------
 *   data1  |  data2  |  data3  |  data4
 * ... ...
 * </pre>
 *
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface Category {

    /**
     * Defines the name of the category.
     *
     * <p>If {@link #i18nSupport() i18nSupport} is true, then the value will be used as the code of i18n
     *
     * @return name of the category
     */
    String value();

    /**
     * Indicates the start header column number of the category(start from 0)
     *
     * @return start header column number
     */
    int start();

    /**
     * Indicates the end header column number of the category(start from 0)
     *
     * @return end header column number
     */
    int end();

    /**
     * If true, the specified {@link Sheet#i18n() i18n method} will be used to internationalize {@link #value() value}
     *
     * @return whether use i18n method for category name
     * @see Sheet#i18n()
     */
    boolean i18nSupport() default true;

    /**
     * Defines the excel style (eg: color, font) of category cell
     *
     * @return category cell style
     */
    CellStyle style() default @CellStyle(useDefault = true);
}
