package com.dream.spring.excel.model;

import com.dream.spring.excel.annotation.Column;

/**
 * @author DreamJM
 */
public class BaseTest {

    @Column
    private String value;

    public BaseTest(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
