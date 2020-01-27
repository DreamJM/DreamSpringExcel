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
 * Defines the category of headers
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
public class HeaderCategory {

    /**
     * Name of the category
     */
    private String name;

    /**
     * Indicates the start header column number of the category(start from 0)
     */
    private int start;

    /**
     * Indicates the end header column number of the category(start from 0)
     */
    private int end;

    /**
     * Style of category cell
     */
    private CustomStyle style;

    private HeaderCategory() {

    }

    public String getName() {
        return name;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public CustomStyle getStyle() {
        return style;
    }

    public static Builder builder(String name, int start, int end) {
        return new Builder(name, start, end);
    }

    public static class Builder {

        private HeaderCategory category;

        private Builder(String name, int start, int end) {
            category = new HeaderCategory();
            category.name = name;
            category.start = start;
            category.end = end;
        }

        public Builder setStyle(CustomStyle style) {
            category.style = style;
            return this;
        }

        public HeaderCategory build() {
            return category;
        }

    }

}
