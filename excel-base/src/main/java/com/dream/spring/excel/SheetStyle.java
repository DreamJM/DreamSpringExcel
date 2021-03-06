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
 * Defines sheet style
 *
 * @author DreamJM
 */
public class SheetStyle {

    /**
     * Sheet name
     */
    private String title;

    /**
     * Default column width (in units of a character width)
     */
    private int defaultWidth = 12;

    /**
     * Header height (in units of a point)
     */
    private int headerHeight = 25;

    /**
     * Category height (in units of a point)
     */
    private int categoryHeight;

    /**
     * Content row height (in units of a point)
     */
    private int contentRowHeight;

    /**
     * Default content cell style
     */
    private CustomStyle defaultStyle;

    /**
     * Default header cell style
     */
    private CustomStyle defaultHeaderStyle;

    /**
     * Default category cell style
     */
    private CustomStyle defaultCategoryStyle;

    /**
     * Column offset of form table
     */
    private int xOffset;

    /**
     * Row offset of form table
     */
    private int yOffset;

    /**
     * If true, freezes the header row
     */
    private boolean freezeHeader = true;

    private SheetStyle() {

    }

    /**
     * @return sheet name
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return default column width (in units of a character width)
     */
    public int getDefaultWidth() {
        return defaultWidth;
    }

    /**
     * @return header height (in units of a point)
     */
    public int getHeaderHeight() {
        return headerHeight;
    }

    /**
     * @return category height (in units of a point)
     */
    public int getCategoryHeight() {
        return categoryHeight;
    }

    /**
     * @return content row height (in units of a point)
     */
    public int getContentRowHeight() {
        return contentRowHeight;
    }

    /**
     * @return default content cell style
     */
    public CustomStyle getDefaultStyle() {
        return defaultStyle;
    }

    /**
     * @return default header cell style
     */
    public CustomStyle getDefaultHeaderStyle() {
        return defaultHeaderStyle;
    }

    /**
     * @return default category cell style
     */
    public CustomStyle getDefaultCategoryStyle() {
        return defaultCategoryStyle;
    }

    /**
     * @return column offset of the form table in sheet
     */
    public int getXOffset() {
        return xOffset;
    }

    /**
     * @return row offset of the form table in sheet
     */
    public int getYOffset() {
        return yOffset;
    }

    /**
     * @return whether should freeze the header row
     */
    public boolean isFreezeHeader() {
        return freezeHeader;
    }

    /**
     * Creates sheet builder
     *
     * @param title sheet name
     * @return sheet builder
     */
    public static Builder builder(String title) {
        return new Builder(title);
    }

    public static class Builder {

        private SheetStyle style;

        private Builder(String title) {
            style = new SheetStyle();
            style.title = title;
        }

        /**
         * Default column width to set. value &lt;= 0 will be ignored
         *
         * @param defaultWidth default column width (in units of a character width)
         * @return sheet builder
         */
        public Builder setDefaultWidth(int defaultWidth) {
            style.defaultWidth = defaultWidth;
            return this;
        }

        /**
         * Header height to set. value &lt;= 0 will be ignored
         *
         * @param headerHeight header height (in units of a point)
         * @return sheet builder
         */
        public Builder setHeaderHeight(int headerHeight) {
            style.headerHeight = headerHeight;
            return this;
        }

        /**
         * Category height to set. value &lt;= 0 will be ignored
         *
         * @param categoryHeight category height (in units of a point)
         * @return sheet builder
         */
        public Builder setCategoryHeight(int categoryHeight) {
            style.categoryHeight = categoryHeight;
            return this;
        }

        /**
         * Content row height to set. value &lt;= 0 will be ignored
         *
         * @param contentRowHeight content row height (in units of a point)
         * @return sheet builder
         */
        public Builder setContentRowHeight(int contentRowHeight) {
            style.contentRowHeight = contentRowHeight;
            return this;
        }

        /**
         * Default cell style to set
         *
         * @param defaultStyle default cell style
         * @return sheet builder
         */
        public Builder setDefaultStyle(CustomStyle defaultStyle) {
            style.defaultStyle = defaultStyle;
            return this;
        }

        /**
         * Default header cell style to set
         *
         * @param defaultHeaderStyle default header cell style
         * @return sheet builder
         */
        public Builder setDefaultHeaderStyle(CustomStyle defaultHeaderStyle) {
            style.defaultHeaderStyle = defaultHeaderStyle;
            return this;
        }

        /**
         * Default category cell style to set
         *
         * @param defaultCategoryStyle default category cell style
         * @return sheet builder
         */
        public Builder setDefaultCategoryStyle(CustomStyle defaultCategoryStyle) {
            style.defaultCategoryStyle = defaultCategoryStyle;
            return this;
        }

        /**
         * Offsets of form table in sheet
         *
         * @param xOffset column offset
         * @param yOffset row offset
         * @return sheet builder
         */
        public Builder setOffset(int xOffset, int yOffset) {
            style.xOffset = xOffset;
            style.yOffset = yOffset;
            return this;
        }

        /**
         * whether should freeze the header row
         *
         * @param freezeHeader If {@code true}, freeze the header row
         * @return sheet builder
         */
        public Builder setFreezeHeader(boolean freezeHeader) {
            style.freezeHeader = freezeHeader;
            return this;
        }

        public SheetStyle build() {
            return style;
        }

    }
}
