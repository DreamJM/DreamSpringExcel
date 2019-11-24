package com.dream.spring.excel;

/**
 * @author DreamJM
 */
public class SheetStyle {

    private String title;

    private int defaultWidth = 12;

    private int headerHeight = 25;

    private int categoryHeight;

    private Integer contentRowHeight;

    private CustomStyle defaultStyle;

    private CustomStyle defaultHeaderStyle;

    private CustomStyle defaultCategoryStyle;

    private int xOffset;

    private int yOffset;

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

    public Integer getContentRowHeight() {
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

        public Builder setContentRowHeight(Integer contentRowHeight) {
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
