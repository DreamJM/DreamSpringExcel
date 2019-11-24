package com.dream.spring.excel;

/**
 * @author DreamJM
 */
public class Column {

    private String header;

    private Integer width;

    private CustomStyle headerStyle;

    private CustomStyle style;

    private Column() {

    }

    public String getHeader() {
        return header;
    }

    public Integer getWidth() {
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

        public Builder setWidth(Integer width) {
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
