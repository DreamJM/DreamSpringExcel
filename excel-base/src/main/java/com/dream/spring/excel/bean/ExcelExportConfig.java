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

package com.dream.spring.excel.bean;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * Global config bean for excel exporting
 *
 * <p>It should be injected into spring container
 *
 * @author DreamJM
 */
public class ExcelExportConfig {

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
    private CellStyle defaultStyle;

    /**
     * Default header cell style
     */
    private CellStyle defaultHeaderStyle;

    /**
     * Default category cell style
     */
    private CellStyle defaultCategoryStyle;

    /**
     * Column offset of form table
     */
    private int columnOffset;

    /**
     * Row offset of form table
     */
    private int rowOffset;

    /**
     * If true, freezes the header row
     */
    private boolean freezeHeader = true;


    public int getDefaultWidth() {
        return defaultWidth;
    }

    public void setDefaultWidth(int defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }

    public int getCategoryHeight() {
        return categoryHeight;
    }

    public void setCategoryHeight(int categoryHeight) {
        this.categoryHeight = categoryHeight;
    }

    public int getContentRowHeight() {
        return contentRowHeight;
    }

    public void setContentRowHeight(int contentRowHeight) {
        this.contentRowHeight = contentRowHeight;
    }

    public CellStyle getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(CellStyle defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    public CellStyle getDefaultHeaderStyle() {
        return defaultHeaderStyle;
    }

    public void setDefaultHeaderStyle(CellStyle defaultHeaderStyle) {
        this.defaultHeaderStyle = defaultHeaderStyle;
    }

    public CellStyle getDefaultCategoryStyle() {
        return defaultCategoryStyle;
    }

    public void setDefaultCategoryStyle(CellStyle defaultCategoryStyle) {
        this.defaultCategoryStyle = defaultCategoryStyle;
    }

    public int getColumnOffset() {
        return columnOffset;
    }

    public void setColumnOffset(int columnOffset) {
        this.columnOffset = columnOffset;
    }

    public int getRowOffset() {
        return rowOffset;
    }

    public void setRowOffset(int rowOffset) {
        this.rowOffset = rowOffset;
    }

    public boolean isFreezeHeader() {
        return freezeHeader;
    }

    public void setFreezeHeader(boolean freezeHeader) {
        this.freezeHeader = freezeHeader;
    }

    public static class CellStyle {
        /**
         * Background color
         */
        private IndexedColors bgColor = IndexedColors.WHITE;

        /**
         * Font color
         */
        private IndexedColors fontColor = IndexedColors.BLACK;

        /**
         * Font family name
         */
        private String fontName;

        /**
         * Font size
         */
        private int fontSize;

        /**
         * Text horizontal alignment
         */
        private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;

        /**
         * Text vertical alignment
         */
        private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;

        public IndexedColors getBgColor() {
            return bgColor;
        }

        public void setBgColor(IndexedColors bgColor) {
            this.bgColor = bgColor;
        }

        public IndexedColors getFontColor() {
            return fontColor;
        }

        public void setFontColor(IndexedColors fontColor) {
            this.fontColor = fontColor;
        }

        public String getFontName() {
            return fontName;
        }

        public void setFontName(String fontName) {
            this.fontName = fontName;
        }

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }

        public HorizontalAlignment getHorizontalAlignment() {
            return horizontalAlignment;
        }

        public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
            this.horizontalAlignment = horizontalAlignment;
        }

        public VerticalAlignment getVerticalAlignment() {
            return verticalAlignment;
        }

        public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
            this.verticalAlignment = verticalAlignment;
        }
    }
}
