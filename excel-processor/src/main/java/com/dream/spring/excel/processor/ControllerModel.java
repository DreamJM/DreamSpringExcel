/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dream.spring.excel.processor;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller information which will be generated as Excel API
 *
 * @author DreamJM
 */
public class ControllerModel {

    /**
     * Controller's full name
     */
    private String fullName;

    /**
     * Controller's package name
     */
    private String packageName;

    /**
     * Controller name
     */
    private String name;

    /**
     * Api tags that collect from swagger @Api annotation
     */
    private List<String> tags = new ArrayList<>();

    /**
     * Api methods to be generated
     */
    private List<ExcelMethodModel> methods = new ArrayList<>();

    /**
     * Autowired components providing excel data
     */
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
        this.tags.addAll(Arrays.stream(tags).filter(tag -> tag != null && !"".equals(tag)).map(String::trim).collect(Collectors.toList()));
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
