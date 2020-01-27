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

package com.dream.spring.excel.importation.exception;

import com.dream.spring.excel.importation.model.CheckRule;

/**
 * Excel data parse exception
 *
 * @author DreamJM
 */
public class ParseException extends Exception {

    public enum Reason {
        /**
         * Common Exception
         */
        Common,
        /**
         * {@link CheckRule#isRequired() required} is true but the cell data is empty
         */
        Empty,
        /**
         * {@link CheckRule#getRegex() regex} is not matched
         */
        Invalid,
        /**
         * {@link CheckRule#getLength() length} is exceeded
         */
        LengthExceeded,
        /**
         * Value is beyond the range of expected values specified by {@link CheckRule#getValidValue()}
         */
        RangeExceeded
    }

    /**
     * Exception reason
     */
    private Reason reason;

    /**
     * Field name of the data
     */
    private String fieldName;

    /**
     * Readable format hint
     */
    private String hint;

    public ParseException(String fieldName) {
        this(fieldName, null);
    }

    public ParseException(String fieldName, String hint) {
        this(Reason.Common, fieldName, hint);
    }

    public ParseException(Reason reason, String fieldName) {
        this(reason, fieldName, null);
    }

    public ParseException(Reason reason, String fieldName, String hint) {
        super();
        this.reason = reason;
        this.fieldName = fieldName;
        this.hint = hint;
    }

    public Reason getReason() {
        return reason;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getHint() {
        return hint;
    }
}
