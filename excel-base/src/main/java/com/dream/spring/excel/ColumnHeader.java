package com.dream.spring.excel;

/**
 * @author DreamJM
 */
public class ColumnHeader {

    private String header;

    private Integer width;

    private CustomStyle style;

    private ColumnHeader() {

    }

    public String getHeader() {
        return header;
    }

    public Integer getWidth() {
        return width;
    }

    public CustomStyle getStyle() {
        return style;
    }

    public static Builder builder(String header) {
        return new Builder(header);
    }

    public static class Builder {

        private ColumnHeader header;

        private Builder(String header) {
            this.header = new ColumnHeader();
            this.header.header = header;
        }

        public Builder setWidth(Integer width) {
            header.width = width;
            return this;
        }

        public Builder setStyle(CustomStyle style) {
            header.style = style;
            return this;
        }

        public ColumnHeader build() {
            return header;
        }

    }
}
