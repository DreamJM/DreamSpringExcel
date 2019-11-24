package com.dream.spring.excel.model;

/**
 * @author DreamJM
 */
public class Component {

    private String childName;

    private String childValue;

    public Component(String childName, String childValue) {
        this.childName = childName;
        this.childValue = childValue;
    }

    public String getChildName() {
        return childName;
    }

    public String getChildValue() {
        return childValue;
    }
}
