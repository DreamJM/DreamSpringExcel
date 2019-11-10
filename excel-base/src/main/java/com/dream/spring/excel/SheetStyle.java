package com.dream.spring.excel;

/**
 * @author DreamJM
 */
public class SheetStyle {

    private String title;

    private int defaultWidth = 12;

    private int defaultHeaderHeight = 25;

    private Integer contentRowHeight;

    private SheetStyle() {

    }

    public String getTitle() {
        return title;
    }

    public int getDefaultWidth() {
        return defaultWidth;
    }

    public int getDefaultHeaderHeight() {
        return defaultHeaderHeight;
    }

    public Integer getContentRowHeight() {
        return contentRowHeight;
    }

    public static class Builder {

        private SheetStyle style;

        private Builder(String title) {
            style = new SheetStyle();
            style.title = title;
        }

        public static Builder builder(String title) {
            return new Builder(title);
        }

        public Builder setDefaultWidth(int defaultWidth) {
            style.defaultWidth = defaultWidth;
            return this;
        }

        public Builder setDefaultHeaderHeight(int defaultHeaderHeight) {
            style.defaultHeaderHeight = defaultHeaderHeight;
            return this;
        }

        public Builder setContentRowHeight(Integer contentRowHeight) {
            style.contentRowHeight = contentRowHeight;
            return this;
        }

        public SheetStyle build() {
            return style;
        }

    }
}
