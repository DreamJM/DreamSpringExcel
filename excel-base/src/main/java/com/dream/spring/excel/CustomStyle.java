package com.dream.spring.excel;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * @author DreamJM
 */
public class CustomStyle {

    private Short bg;

    private Short fontColor;

    private String fontName;

    private int fontSize;

    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;

    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;

    private CustomStyle() {

    }

    public Short getBg() {
        return bg;
    }

    public Short getFontColor() {
        return fontColor;
    }

    public String getFontName() {
        return fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

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

        public Builder setBg(Short bg) {
            style.bg = bg;
            return this;
        }

        public Builder setFontColor(Short fontColor) {
            style.fontColor = fontColor;
            return this;
        }

        public Builder setFontName(String fontName) {
            style.fontName = fontName;
            return this;
        }

        public Builder setFontSize(int fontSize) {
            style.fontSize = fontSize;
            return this;
        }

        public Builder setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
            style.horizontalAlignment = horizontalAlignment;
            return this;
        }

        public Builder setVerticalAlignment(VerticalAlignment verticalAlignment) {
            style.verticalAlignment = verticalAlignment;
            return this;
        }

        public CustomStyle build() {
            return style;
        }

    }
}
