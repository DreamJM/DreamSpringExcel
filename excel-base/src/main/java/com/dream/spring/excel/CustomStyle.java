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

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * Customizes style of cell
 *
 * @author DreamJM
 */
public class CustomStyle {

    /**
     * Background color
     */
    private Short bg;

    /**
     * Font color
     */
    private Short fontColor;

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

    private CustomStyle() {

    }

    /**
     * @return background color({@link org.apache.poi.ss.usermodel.IndexedColors})
     */
    public Short getBg() {
        return bg;
    }

    /**
     * @return font color({@link org.apache.poi.ss.usermodel.IndexedColors})
     */
    public Short getFontColor() {
        return fontColor;
    }

    /**
     * @return font family name
     */
    public String getFontName() {
        return fontName;
    }

    /**
     * @return font size
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * @return text horizontal alignment
     */
    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * @return text vertical alignment
     */
    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CustomStyle style;

        private Builder() {
            style = new CustomStyle();
        }

        /**
         * Cell background color to set. Value should be {@link IndexedColors#getIndex()} Indexed Color's index}
         *
         * @param bg cell background color to set
         * @return cell style builder
         */
        public Builder setBg(Short bg) {
            style.bg = bg;
            return this;
        }

        /**
         * Cell font color to set. Value should be {@link IndexedColors#getIndex()} Indexed Color's index}
         *
         * @param fontColor cell font color to set
         * @return cell style builder
         */
        public Builder setFontColor(Short fontColor) {
            style.fontColor = fontColor;
            return this;
        }

        /**
         * Cell font family name to set.
         *
         * @param fontName Cell font family name
         * @return cell style builder
         */
        public Builder setFontName(String fontName) {
            style.fontName = fontName;
            return this;
        }

        /**
         * Cell font size to set
         *
         * @param fontSize cell font size
         * @return cell style builder
         */
        public Builder setFontSize(int fontSize) {
            style.fontSize = fontSize;
            return this;
        }

        /**
         * Text horizontal alignment to set
         *
         * @param horizontalAlignment text horizontal alignment
         * @return cell style builder
         */
        public Builder setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
            style.horizontalAlignment = horizontalAlignment;
            return this;
        }

        /**
         * Text vertical alignment to set
         *
         * @param verticalAlignment text horizontal alignment
         * @return cell style builder
         */
        public Builder setVerticalAlignment(VerticalAlignment verticalAlignment) {
            style.verticalAlignment = verticalAlignment;
            return this;
        }

        public CustomStyle build() {
            return style;
        }

    }
}
