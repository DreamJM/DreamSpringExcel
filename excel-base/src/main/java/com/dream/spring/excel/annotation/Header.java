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
 * Defines the excel form header's data and appearance
 *
 * @author DreamJM
 * @see Sheet#headers()
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface Header {

    /**
     * The name of the header
     *
     * <p>If {@link #i18nSupport() i18nSupport} is true, then the value will be used as the code of i18n
     *
     * @return name of the header
     */
    String value();

    /**
     * The field name in the {@link Sheet Sheet} annotated class that will be used as the value of the corresponding column
     *
     * <ul>
     * <li>Field should have getXxx method</li>
     * <li>If field type is boolean, then you may use isXxx method as getter. In this situation, you can use 'isXxx' as the field name</li>
     * <li>If the field you want to use is the sub field of an object field, you can use '.' to join them.
     * Example: field = "field1.field2"
     * </li>
     * </ul>
     *
     * @return field name
     */
    String field() default "";

    /**
     * @return The width of the column in units of a character width
     */
    int width() default 0;

    /**
     * @return The cell style(eg: font, color, etc.) of the header
     */
    CellStyle style() default @CellStyle(useDefault = true);

    /**
     * @return If true, the specified {@link Sheet#i18n() i18n method} will be used to internationalize {@link #value() value}
     * @see Sheet#i18n()
     */
    boolean i18nSupport() default true;

    /**
     * @return the extra decoration for the header
     */
    HeaderNote note() default @HeaderNote;
}
