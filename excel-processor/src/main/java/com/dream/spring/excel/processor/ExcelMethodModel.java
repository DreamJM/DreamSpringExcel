package com.dream.spring.excel.processor;

import com.dream.spring.excel.annotation.ExcelExport;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * @author DreamJM
 */
public class ExcelMethodModel {

    private ExcelExport annotation;

    private ExecutableElement methodElement;

    private TypeElement ref;

    public ExcelMethodModel(ExcelExport annotation, ExecutableElement methodElement, TypeElement ref) {
        this.annotation = annotation;
        this.methodElement = methodElement;
        this.ref = ref;
    }

    public ExcelExport getAnnotation() {
        return annotation;
    }

    public ExecutableElement getMethodElement() {
        return methodElement;
    }

    public TypeElement getRef() {
        return ref;
    }
}
