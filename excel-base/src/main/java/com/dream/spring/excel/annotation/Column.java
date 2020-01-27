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

import org.apache.poi.ss.usermodel.CellType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Customizes the column's data format and appearance
 *
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Column {

    /**
     * @return Cell data type
     * @see CellType
     */
    CellType type() default CellType.STRING;

    /**
     * Represents the default cell style for the annotated column
     * <p>If not specified, the style specified in {@link Sheet#defaultStyle() Sheet#defaultStyle} will be used as default.
     *
     * @return the default cell style for the annotated column
     */
    CellStyle style() default @CellStyle(useDefault = true);

    /**
     * Customizes the converter method of the column data to convert raw data to the value showed in excel
     *
     * @return converter method information
     */
    Converter converter() default @Converter;

    /**
     * @return If true, the specified {@link Sheet#i18n() i18n method} will be used to internationalize the column data
     * @see Sheet#i18n()
     */
    boolean i18nSupport() default false;

    /**
     * Specifies the cells style on some condition.
     * <p>If you want to mark the cell which has value greater than 1 with red font color:
     * <pre class="code">
     * cellStyles = @CellItemStyle(condition = "{value} &gt; 1", style = @CellStyle(fontColor = IndexedColors.RED))
     * </pre>
     *
     * @return specific cell styles
     */
    CellItemStyle[] cellStyles() default {};
}
