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
     * Default column width
     */
    private int defaultWidth = 12;

    /**
     * Header height
     */
    private int headerHeight = 25;

    /**
     * Category height
     */
    private int categoryHeight;

    /**
     * Content row height
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

    public String getTitle() {
        return title;
    }

    public int getDefaultWidth() {
        return defaultWidth;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public int getCategoryHeight() {
        return categoryHeight;
    }

    public int getContentRowHeight() {
        return contentRowHeight;
    }

    public CustomStyle getDefaultStyle() {
        return defaultStyle;
    }

    public CustomStyle getDefaultHeaderStyle() {
        return defaultHeaderStyle;
    }

    public CustomStyle getDefaultCategoryStyle() {
        return defaultCategoryStyle;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public boolean isFreezeHeader() {
        return freezeHeader;
    }

    public static Builder builder(String title) {
        return new Builder(title);
    }

    public static class Builder {

        private SheetStyle style;

        private Builder(String title) {
            style = new SheetStyle();
            style.title = title;
        }

        public Builder setDefaultWidth(int defaultWidth) {
            style.defaultWidth = defaultWidth;
            return this;
        }

        public Builder setHeaderHeight(int headerHeight) {
            style.headerHeight = headerHeight;
            return this;
        }

        public Builder setCategoryHeight(int categoryHeight) {
            style.categoryHeight = categoryHeight;
            return this;
        }

        public Builder setContentRowHeight(int contentRowHeight) {
            style.contentRowHeight = contentRowHeight;
            return this;
        }

        public Builder setDefaultStyle(CustomStyle defaultStyle) {
            style.defaultStyle = defaultStyle;
            return this;
        }

        public Builder setDefaultHeaderStyle(CustomStyle defaultHeaderStyle) {
            style.defaultHeaderStyle = defaultHeaderStyle;
            return this;
        }

        public Builder setDefaultCategoryStyle(CustomStyle defaultCategoryStyle) {
            style.defaultCategoryStyle = defaultCategoryStyle;
            return this;
        }

        public Builder setOffset(int xOffset, int yOffset) {
            style.xOffset = xOffset;
            style.yOffset = yOffset;
            return this;
        }

        public Builder setFreezeHeader(boolean freezeHeader) {
            style.freezeHeader = freezeHeader;
            return this;
        }

        public SheetStyle build() {
            return style;
        }

    }
}
