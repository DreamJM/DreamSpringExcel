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
     * Whether the cell value is required. If broken, {@link com.dream.spring.excel.importation.exception.ParseException} will be thrown
     * with reason {@link com.dream.spring.excel.importation.exception.ParseException.Reason#Empty}
     */
    private boolean required;

    /**
     * Max length limitation. If broken, {@link com.dream.spring.excel.importation.exception.ParseException} will be thrown
     * with reason {@link com.dream.spring.excel.importation.exception.ParseException.Reason#LengthExceeded}
     */
    private int length;

    /**
     * Name or I18n code of the column name to be checked
     */
    private String name;

    /**
     * Regex expression. If not matched, {@link com.dream.spring.excel.importation.exception.ParseException} will be thrown
     * with reason {@link com.dream.spring.excel.importation.exception.ParseException.Reason#Invalid}
     */
    private String regex;

    /**
     * Format hint or hint code for the regex expression
     */
    private String formatHint;

    /**
     * Regex group number that will be used to retrieve the value and check with {@link #validValue Valid Value}
     */
    private Integer regexGroupNum;

    /**
     * Expected ranged of valid value. If not contained in the set, {@link com.dream.spring.excel.importation.exception.ParseException}
     * will be thrown with reason {@link com.dream.spring.excel.importation.exception.ParseException.Reason#RangeExceeded}
     */
    private Set<String> validValue;

    /**
     * When {@link #required} is false and cell data is empty, this default value will be used
     */
    private String defaultValue = "";

    private CheckRule() {

    }

    /**
     * Creates a Check Rule builder
     *
     * @param name Name or I18n code of the column name to be checked
     * @return Check Rule builder
     */
    public static Builder builder(String name) {
        return new Builder(name);
    }

    /**
     * Whether the cell value is required. If broken, {@link com.dream.spring.excel.importation.exception.ParseException} will be thrown
     * with reason {@link com.dream.spring.excel.importation.exception.ParseException.Reason#Empty}
     *
     * @return {@code true} if cell value is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Max length limitation. If broken, {@link com.dream.spring.excel.importation.exception.ParseException} will be thrown
     * with reason {@link com.dream.spring.excel.importation.exception.ParseException.Reason#LengthExceeded}
     *
     * @return max length of cell value
     */
    public int getLength() {
        return length;
    }

    /**
     * @return Name or I18n code of the column name to be checked
     */
    public String getName() {
        return name;
    }

    /**
     * Regex expression. If not matched, {@link com.dream.spring.excel.importation.exception.ParseException} will be thrown
     * with reason {@link com.dream.spring.excel.importation.exception.ParseException.Reason#Invalid}
     *
     * @return Regex expression
     */
    public String getRegex() {
        return regex;
    }

    /**
     * @return Format hint or hint code for the regex expression
     */
    public String getFormatHint() {
        return formatHint;
    }

    /**
     * Regex group number that will be used to retrieve the value and check with {@link #getValidValue() Valid Value Set}
     *
     * @return Expected value's regex group number
     */
    public Integer getRegexGroupNum() {
        return regexGroupNum;
    }

    /**
     * Expected ranged of valid value. If not contained in the set, {@link com.dream.spring.excel.importation.exception.ParseException}
     * will be thrown with reason {@link com.dream.spring.excel.importation.exception.ParseException.Reason#RangeExceeded}
     *
     * @return valid value set
     */
    public Set<String> getValidValue() {
        return validValue;
    }

    /**
     * When {@link #isRequired()} is false and cell data is empty, this default value will be used
     *
     * @return default value when empty
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    public static class Builder {
        private CheckRule rule;

        private Builder(String name) {
            rule = new CheckRule();
            rule.name = name;
        }

        /**
         * Whether the cell value is required. If broken, {@link com.dream.spring.excel.importation.exception.ParseException} will be thrown
         * with reason {@link com.dream.spring.excel.importation.exception.ParseException.Reason#Empty}
         *
         * @param required Whether the cell value is required
         * @return check rule builder
         */
        public Builder setRequired(boolean required) {
            rule.required = required;
            return this;
        }

        /**
         * Max length limitation. If broken, {@link com.dream.spring.excel.importation.exception.ParseException} will be thrown
         * with reason {@link com.dream.spring.excel.importation.exception.ParseException.Reason#LengthExceeded}
         *
         * @param length max length of cell value
         * @return check rule builder
         */
        public Builder setLength(int length) {
            rule.length = length;
            return this;
        }

        /**
         * Regex expression. If not matched, {@link com.dream.spring.excel.importation.exception.ParseException} will be thrown
         * with reason {@link com.dream.spring.excel.importation.exception.ParseException.Reason#Invalid}
         *
         * @param regex regex expression to set
         * @return check rule builder
         */
        public Builder setRegex(String regex) {
            rule.regex = regex;
            return this;
        }

        /**
         * Format hint or hint code for the regex expression
         *
         * @param formatHint format hint to set
         * @return check rule builder
         */
        public Builder setFormatHint(String formatHint) {
            rule.formatHint = formatHint;
            return this;
        }

        /**
         * Regex group number that will be used to retrieve the value and check with {@link #getValidValue() Valid Value Set}
         *
         * @param regexGroupNum Expected value's regex group number
         * @return check rule builder
         */
        public Builder setRegexGroupNum(int regexGroupNum) {
            rule.regexGroupNum = regexGroupNum;
            return this;
        }

        /**
         * Expected ranged of valid value. If not contained in the set, {@link com.dream.spring.excel.importation.exception.ParseException}
         * will be thrown with reason {@link com.dream.spring.excel.importation.exception.ParseException.Reason#RangeExceeded}
         *
         * @param validValue valid value set to check with
         * @return check rule builder
         */
        public Builder setValidValue(Set<String> validValue) {
            rule.validValue = validValue;
            return this;
        }

        /**
         * When {@link #isRequired()} is false and cell data is empty, this default value will be used
         *
         * @param defaultValue default value when empty
         * @return check rule builder
         */
        public Builder setDefaultValue(String defaultValue) {
            rule.defaultValue = defaultValue;
            return this;
        }

        public CheckRule build() {
            return rule;
        }
    }
}
