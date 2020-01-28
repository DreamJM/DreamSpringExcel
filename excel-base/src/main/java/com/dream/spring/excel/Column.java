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

/**
 * Specifies style for column
 *
 * @author DreamJM
 */
public class Column {

    /**
     * Column header
     */
    private String header;

    /**
     * Column width (in units of a character width)
     */
    private int width;

    /**
     * Header style
     */
    private CustomStyle headerStyle;

    /**
     * Default content style
     */
    private CustomStyle style;

    private Column() {

    }

    /**
     * @return column header name
     */
    public String getHeader() {
        return header;
    }

    /**
     * @return column width(in units of a character width)
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return header style
     */
    public CustomStyle getHeaderStyle() {
        return headerStyle;
    }

    /**
     * @return content cell style
     */
    public CustomStyle getStyle() {
        return style;
    }

    /**
     * Creates Column Builder
     *
     * @param header column header name
     * @return Column Builder
     */
    public static Builder builder(String header) {
        return new Builder(header);
    }

    public static class Builder {

        private Column column;

        private Builder(String column) {
            this.column = new Column();
            this.column.header = column;
        }

        /**
         * Column width (in units of a character width) to set. Value &lt;= 0 will be ignored
         *
         * @param width column width (in units of a character width)
         * @return Column Builder
         */
        public Builder setWidth(int width) {
            column.width = width;
            return this;
        }

        /**
         * Header cell style to set
         *
         * @param headerStyle header cell style
         * @return Column Builder
         */
        public Builder setHeaderStyle(CustomStyle headerStyle) {
            column.headerStyle = headerStyle;
            return this;
        }

        /**
         * Content cell style to set
         *
         * @param style content cell style
         * @return Column Builder
         */
        public Builder setStyle(CustomStyle style) {
            column.style = style;
            return this;
        }

        public Column build() {
            return column;
        }

    }
}
