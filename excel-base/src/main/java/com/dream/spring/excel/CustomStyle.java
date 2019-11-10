package com.dream.spring.excel;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * @author DreamJM
 */
public class CustomStyle {

    private Short bg;

    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;

    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;

    private CustomStyle() {

    }

    public Short getBg() {
        return bg;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public static class Builder {
        private CustomStyle style;

        private Builder() {
            style = new CustomStyle();
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder setBg(Short bg) {
            style.bg = bg;
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
