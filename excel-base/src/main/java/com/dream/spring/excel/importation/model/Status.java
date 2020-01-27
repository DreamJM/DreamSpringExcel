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

import java.util.Date;
import java.util.List;

/**
 * Excel importing progress status
 *
 * @author DreamJM
 */
public class Status {

    public enum ExcelStatus {

        /**
         * Under preparing
         */
        PREPARING(0),
        /**
         * Importing
         */
        IMPORTING(1),
        /**
         * Import completed
         */
        COMPLETE(2),
        /**
         * Import failed
         */
        FAIL(-1),
        /**
         * IO Exception
         */
        IO_ERROR(-2);

        private int value;

        ExcelStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Importing status of excel
     */
    private ExcelStatus status;

    /**
     * Total row count of excel
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
     * Status of each sheet
     */
    private List<SheetStatus> sheets;

    /**
     * Importing start time
     */
    private Date startTime;

    /**
     * Importing end time
     */
    private Date endTime;

    public Status() {
    }

    public Status(ExcelStatus status, List<SheetStatus> sheets, Date startTime, Date endTime) {
        this.status = status;
        if (sheets != null) {
            for (SheetStatus sheet : sheets) {
                this.totalCnt += sheet.getTotalCnt();
                this.parseCnt += sheet.getParseCnt();
                this.writeCnt += sheet.getWriteCnt();
            }
        }
        this.sheets = sheets;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ExcelStatus getStatus() {
        return status;
    }

    public void setStatus(ExcelStatus status) {
        this.status = status;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }

    public int getParseCnt() {
        return parseCnt;
    }

    public void setParseCnt(int parseCnt) {
        this.parseCnt = parseCnt;
    }

    public int getWriteCnt() {
        return writeCnt;
    }

    public void setWriteCnt(int writeCnt) {
        this.writeCnt = writeCnt;
    }

    public List<SheetStatus> getSheets() {
        return sheets;
    }

    public void setSheets(List<SheetStatus> sheets) {
        this.sheets = sheets;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Status{" +
                "status=" + status +
                ", totalCnt=" + totalCnt +
                ", parseCnt=" + parseCnt +
                ", writeCnt=" + writeCnt +
                ", sheets=" + sheets +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
