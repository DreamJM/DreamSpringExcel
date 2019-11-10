package com.dream.spring.excel.processor;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author DreamJM
 */
public class ControllerModel {

    private String fullName;

    private String packageName;

    private String name;

    private List<String> tags = new ArrayList<>();

    private List<ExcelMethodModel> methods = new ArrayList<>();

    private List<TypeElement> refs = new ArrayList<>();

    public ControllerModel(String fullName) {
        this.fullName = fullName;
        this.packageName = fullName.substring(0, fullName.lastIndexOf("."));
        this.name = fullName.substring(packageName.length() + 1);
    }

    public String getFullName() {
        return fullName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public void addMethod(ExcelMethodModel method) {
        methods.add(method);
    }

    public List<ExcelMethodModel> getMethods() {
        return methods;
    }

    public void addTags(String[] tags) {
        this.tags.addAll(Arrays.stream(tags).filter(tag -> tag != null && !"".equals(tag)).collect(Collectors.toList()));
    }

    public List<String> getTags() {
        return tags;
    }

    public void addRef(TypeElement ref) {
        if (!refs.contains(ref)) {
            refs.add(ref);
        }
    }

    public List<TypeElement> getRefs() {
        return refs;
    }

    public String getRefName(TypeElement ref) {
        int index = this.refs.indexOf(ref);
        return "ref" + index;
    }
}
