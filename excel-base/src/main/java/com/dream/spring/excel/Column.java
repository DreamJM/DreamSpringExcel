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
     * Column width
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

    public String getHeader() {
        return header;
    }

    public int getWidth() {
        return width;
    }

    public CustomStyle getHeaderStyle() {
        return headerStyle;
    }

    public CustomStyle getStyle() {
        return style;
    }

    public static Builder builder(String header) {
        return new Builder(header);
    }

    public static class Builder {

        private Column column;

        private Builder(String column) {
            this.column = new Column();
            this.column.header = column;
        }

        public Builder setWidth(int width) {
            column.width = width;
            return this;
        }

        public Builder setHeaderStyle(CustomStyle headerStyle) {
            column.headerStyle = headerStyle;
            return this;
        }

        public Builder setStyle(CustomStyle style) {
            column.style = style;
            return this;
        }

        public Column build() {
            return column;
        }

    }
}
