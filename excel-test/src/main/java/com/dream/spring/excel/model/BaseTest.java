package com.dream.spring.excel.model;

import com.dream.spring.excel.annotation.Cell;

/**
 * @author DreamJM
 */
public class BaseTest {

    @Cell
    private String value;

    public BaseTest(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
