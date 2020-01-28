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

import java.util.List;

/**
 * Sheet parsing progress status
 *
 * @author DreamJM
 */
public class SheetStatus {

    public enum Status {
        /**
         * Under preparing
         */
        PREPARING,
        /**
         * Importing
         */
        IMPORTING,
        /**
         * Import completed
         */
        COMPLETE
    }

    /**
     * Parsed sheet number in the imported excel
     */
    private int sheetNum;

    /**
     * Sheet name
     */
    private String sheetName;

    /**
     * Total row count of the sheet
     */
    private int totalCnt;

    /**
     * Parsed row count
     */
    private int parseCnt;

    /**
     * Row count that has been written into storage
     */
    private int writeCnt;

    /**
     * Error rows information
     */
    private List<ErrorLog> errors;

    /**
     * Current status of sheet importing
     */
    private Status status;

    public SheetStatus(Status status, int sheetNum, String sheetName, int totalCnt, int parseCnt, int writeCnt, List<ErrorLog> errors) {
        this.status = status;
        this.sheetNum = sheetNum;
        this.sheetName = sheetName;
        this.totalCnt = totalCnt;
        this.parseCnt = parseCnt;
        this.writeCnt = writeCnt;
        this.errors = errors;
    }

    /**
     * @return parsed sheet number in the imported excel
     */
    public int getSheetNum() {
        return sheetNum;
    }

    /**
     * @return sheet name
     */
    public String getSheetName() {
        return sheetName;
    }

    /**
     * @return total row count of the sheet
     */
    public int getTotalCnt() {
        return totalCnt;
    }

    /**
     * @return parsed row count
     */
    public int getParseCnt() {
        return parseCnt;
    }

    /**
     * @return row count that has been written into storage
     */
    public int getWriteCnt() {
        return writeCnt;
    }

    /**
     * @return error rows information
     */
    public List<ErrorLog> getErrors() {
        return errors;
    }

    /**
     * @return current status of sheet importing
     */
    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "SheetStatus{" +
                "sheetNum=" + sheetNum +
                ", sheetName='" + sheetName + '\'' +
                ", totalCnt=" + totalCnt +
                ", parseCnt=" + parseCnt +
                ", writeCnt=" + writeCnt +
                ", errors=" + errors +
                ", status=" + status +
                '}';
    }
}
