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

package com.dream.spring.excel.importation.model;

import java.util.Set;

/**
 * The checking rules of excel cell data to be imported
 *
 * @author DreamJM
 */
public class CheckRule {

    /**
     * Whether the cell data is required
     */
    private boolean required;

    /**
     * Max length limitation
     */
    private int length;

    /**
     * Name or I18n code of the column name to be checked
     */
    private String name;

    /**
     * Regex expression
     */
    private String regex;

    /**
     * Format hint or hint code for the regex expression
     */
    private String formatHint;

    /**
     * Regex group number that will be retrieved to check with {@link #validValue Valid Value}
     */
    private Integer regexGroupNum;

    /**
     * Expected ranged of valid value
     */
    private Set<String> validValue;

    /**
     * When {@link #required} is false and cell data is empty, this default value will be used
     */
    private String defaultValue = "";

    private CheckRule() {

    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public boolean isRequired() {
        return required;
    }

    public int getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public String getRegex() {
        return regex;
    }

    public String getFormatHint() {
        return formatHint;
    }

    public Integer getRegexGroupNum() {
        return regexGroupNum;
    }

    public Set<String> getValidValue() {
        return validValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public static class Builder {
        private CheckRule rule;

        private Builder(String name) {
            rule = new CheckRule();
            rule.name = name;
        }

        public Builder setRequired(boolean required) {
            rule.required = required;
            return this;
        }

        public Builder setLength(int length) {
            rule.length = length;
            return this;
        }

        public Builder setRegex(String regex) {
            rule.regex = regex;
            return this;
        }

        public Builder setFormatHint(String formatHint) {
            rule.formatHint = formatHint;
            return this;
        }

        public Builder setRegexGroupNum(int regexGroupNum) {
            rule.regexGroupNum = regexGroupNum;
            return this;
        }

        public Builder setValidValue(Set<String> validValue) {
            rule.validValue = validValue;
            return this;
        }

        public Builder setDefaultValue(String defaultValue) {
            rule.defaultValue = defaultValue;
            return this;
        }

        public CheckRule build() {
            return rule;
        }
    }
}
