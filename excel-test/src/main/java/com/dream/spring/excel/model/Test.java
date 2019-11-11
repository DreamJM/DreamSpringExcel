package com.dream.spring.excel.model;

import com.dream.spring.excel.annotation.Cell;
import com.dream.spring.excel.annotation.Header;
import com.dream.spring.excel.annotation.Sheet;

import java.util.Date;

/**
 * @author DreamJM
 */
@Sheet(value = "Test", headers = {@Header(value = "名称", field = "name", width = 15), @Header(value = "值", field = "value"),
        @Header(value = "日期", field = "date", width = 20)})
public class Test extends BaseTest {

    private String name;

    private int type;

    @Cell
    private Date date;

    private Component component;

    public Test(String name, String value, int type, Date date, Component component) {
        super(value);
        this.name = name;
        this.type = type;
        this.date = date;
        this.component = component;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public Component getComponent() {
        return component;
    }
}
