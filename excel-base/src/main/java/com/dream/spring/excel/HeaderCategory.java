package com.dream.spring.excel;

/**
 * @author DreamJM
 */
public class HeaderCategory {

    private String name;

    private int start;

    private int end;

    private CustomStyle style;

    private HeaderCategory() {

    }

    public String getName() {
        return name;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public CustomStyle getStyle() {
        return style;
    }

    public static Builder builder(String name, int start, int end) {
        return new Builder(name, start, end);
    }

    public static class Builder {

        private HeaderCategory category;

        private Builder(String name, int start, int end) {
            category = new HeaderCategory();
            category.name = name;
            category.start = start;
            category.end = end;
        }

        public Builder setStyle(CustomStyle style) {
            category.style = style;
            return this;
        }

        public HeaderCategory build() {
            return category;
        }

    }

}
