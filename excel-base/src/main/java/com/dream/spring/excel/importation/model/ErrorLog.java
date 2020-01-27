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

import com.dream.spring.excel.importation.exception.ParseException;

/**
 * Error description of the imported sheet data
 *
 * @author DreamJM
 */
public class ErrorLog {

    /**
     * Error occurred line number
     */
    private int row;

    /**
     * Error reason
     */
    private ParseException.Reason reason;

    /**
     * Error field name
     */
    private String fieldName;

    /**
     * Error field format hint
     */
    private String hint;

    public ErrorLog(int row, ParseException exception) {
        this.row = row;
        this.reason = exception.getReason();
        this.fieldName = exception.getFieldName();
        this.hint = exception.getHint();
    }

    public int getRow() {
        return row;
    }

    public ParseException.Reason getReason() {
        return reason;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getHint() {
        return hint;
    }

    @Override
    public String toString() {
        return "ErrorLog{" +
                "row=" + row +
                ", reason=" + reason +
                ", fieldName='" + fieldName + '\'' +
                ", hint='" + hint + '\'' +
                '}';
    }
}
