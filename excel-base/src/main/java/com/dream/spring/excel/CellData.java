package com.dream.spring.excel;

import org.apache.poi.ss.usermodel.CellType;

/**
 * @author DreamJM
 */
public class CellData {

    private String value;

    private CellType type;

    private CustomStyle style;

    private CellData() {

    }

    public String getValue() {
        return value;
    }

    public CellType getType() {
        return type;
    }

    public CustomStyle getStyle() {
        return style;
    }

    public static class Builder {
        private CellData cell;

        private Builder(String value) {
            this.cell = new CellData();
            this.cell.value = value;
        }

        public static Builder builder(String value) {
            return new Builder(value);
        }

        public Builder setType(CellType type) {
            cell.type = type;
            return this;
        }

        public Builder setStyle(CustomStyle style) {
            cell.style = style;
            return this;
        }

        public CellData build() {
            return cell;
        }

    }
}
