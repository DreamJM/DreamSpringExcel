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

package com.dream.spring.excel;

import org.apache.poi.ss.usermodel.CellType;

/**
 * Data of excel cell
 *
 * @author DreamJM
 */
public class CellData {

    /**
     * Cell Value
     */
    private String value;

    /**
     * Cell Type
     */
    private CellType type;

    /**
     * Cell Style
     */
    private CustomStyle style;

    private CellData() {

    }

    /**
     * @return cell value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return cell type
     */
    public CellType getType() {
        return type;
    }

    /**
     * @return cell style
     */
    public CustomStyle getStyle() {
        return style;
    }

    /**
     * Creates Cell Data Builder
     *
     * @param value cell value
     * @return Cell Data Builder
     */
    public static Builder builder(String value) {
        return new Builder(value);
    }

    public static class Builder {
        private CellData cell;

        private Builder(String value) {
            this.cell = new CellData();
            this.cell.value = value;
        }

        /**
         * Cell type
         *
         * @param type cell type to set
         * @return Cell Data Builder
         */
        public Builder setType(CellType type) {
            cell.type = type;
            return this;
        }

        /**
         * Cell style
         *
         * @param style cell style to set
         * @return Cell Data Builder
         */
        public Builder setStyle(CustomStyle style) {
            cell.style = style;
            return this;
        }

        public CellData build() {
            return cell;
        }

    }
}
