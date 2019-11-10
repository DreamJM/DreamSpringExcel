package com.dream.spring.excel;

import com.dream.spring.excel.annotation.Cell;
import com.dream.spring.excel.annotation.Header;
import com.dream.spring.excel.annotation.Sheet;

/**
 * @author DreamJM
 */
@Sheet(value = "Test", headers = {@Header(value = "名称", field = "name", width = 15), @Header(value = "值", field = "value", width = 12),
        @Header(value = "日期", field = "date", width = 20)})
public class Test {

    private String name;

    private String value;

    @Cell
    private String date;

    public Test(String name, String value, String date) {
        this.name = name;
        this.value = value;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
