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

    /**
     * @return Default column width (in units of a character width)
     */
    public int getDefaultWidth() {
        return defaultWidth;
    }

    /**
     * @param defaultWidth default column width to set (in units of a character width)
     */
    public void setDefaultWidth(int defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    /**
     * @return Header row height (in units of a point)
     */
    public int getHeaderHeight() {
        return headerHeight;
    }

    /**
     * @param headerHeight Header row height to set (in units of a point)
     */
    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }

    /**
     * @return Category row height (in units of a point)
     */
    public int getCategoryHeight() {
        return categoryHeight;
    }

    /**
     * @param categoryHeight Category row height to set (in units of a point)
     */
    public void setCategoryHeight(int categoryHeight) {
        this.categoryHeight = categoryHeight;
    }

    /**
     * @return Content row height (in units of a point)
     */
    public int getContentRowHeight() {
        return contentRowHeight;
    }

    /**
     * @param contentRowHeight Content row height to set(in units of a point)
     */
    public void setContentRowHeight(int contentRowHeight) {
        this.contentRowHeight = contentRowHeight;
    }

    /**
     * @return Default content cell style
     */
    public CellStyle getDefaultStyle() {
        return defaultStyle;
    }

    /**
     * @param defaultStyle Default content cell style to set
     */
    public void setDefaultStyle(CellStyle defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    /**
     * @return Default header cell style
     */
    public CellStyle getDefaultHeaderStyle() {
        return defaultHeaderStyle;
    }

    /**
     * @param defaultHeaderStyle Default header cell style to set
     */
    public void setDefaultHeaderStyle(CellStyle defaultHeaderStyle) {
        this.defaultHeaderStyle = defaultHeaderStyle;
    }

    /**
     * @return Default category cell style to set
     */
    public CellStyle getDefaultCategoryStyle() {
        return defaultCategoryStyle;
    }

    /**
     * @param defaultCategoryStyle Default category cell style to set
     */
    public void setDefaultCategoryStyle(CellStyle defaultCategoryStyle) {
        this.defaultCategoryStyle = defaultCategoryStyle;
    }

    /**
     * @return Column offset of form table in excel sheet
     */
    public int getColumnOffset() {
        return columnOffset;
    }

    /**
     * @param columnOffset Column offset of form table in excel sheet to set
     */
    public void setColumnOffset(int columnOffset) {
        this.columnOffset = columnOffset;
    }

    /**
     * @return Row offset of form table in excel sheet
     */
    public int getRowOffset() {
        return rowOffset;
    }

    /**
     * @param rowOffset Row offset of form table in excel sheet to set
     */
    public void setRowOffset(int rowOffset) {
        this.rowOffset = rowOffset;
    }

    /**
     * @return {@code true} if the header row is frozen
     */
    public boolean isFreezeHeader() {
        return freezeHeader;
    }

    /**
     * @param freezeHeader whether to freeze the header row
     */
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

        /**
         * @return background color using enum of {@link IndexedColors}
         */
        public IndexedColors getBgColor() {
            return bgColor;
        }

        /**
         * @param bgColor background color to set using enum of {@link IndexedColors}
         */
        public void setBgColor(IndexedColors bgColor) {
            this.bgColor = bgColor;
        }

        /**
         * @return font color using enum of {@link IndexedColors}
         */
        public IndexedColors getFontColor() {
            return fontColor;
        }

        /**
         * @param fontColor font color to set using enum of {@link IndexedColors}
         */
        public void setFontColor(IndexedColors fontColor) {
            this.fontColor = fontColor;
        }

        /**
         * @return font family name
         */
        public String getFontName() {
            return fontName;
        }

        /**
         * @param fontName font family name to set
         */
        public void setFontName(String fontName) {
            this.fontName = fontName;
        }

        /**
         * @return font size
         */
        public int getFontSize() {
            return fontSize;
        }

        /**
         * @param fontSize font size to set (number &lt;= 0 will be ignored)
         */
        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }

        /**
         * @return text horizontal alignment
         */
        public HorizontalAlignment getHorizontalAlignment() {
            return horizontalAlignment;
        }

        /**
         * @param horizontalAlignment text horizontal alignment to set
         */
        public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
            this.horizontalAlignment = horizontalAlignment;
        }

        /**
         * @return text vertical alignment
         */
        public VerticalAlignment getVerticalAlignment() {
            return verticalAlignment;
        }

        /**
         * @param verticalAlignment text vertical alignment to set
         */
        public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
            this.verticalAlignment = verticalAlignment;
        }
    }
}
