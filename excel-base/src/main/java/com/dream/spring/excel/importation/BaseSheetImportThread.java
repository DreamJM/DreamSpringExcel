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

package com.dream.spring.excel.importation;

import com.dream.spring.excel.importation.exception.ParseException;
import com.dream.spring.excel.importation.model.ErrorLog;
import com.dream.spring.excel.importation.model.RowData;
import com.dream.spring.excel.importation.model.RowWrapper;
import com.dream.spring.excel.importation.model.SheetStatus;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Thread used for sheet importing
 *
 * @param <T> Row Data Type
 * @author DreamJM
 */
public abstract class BaseSheetImportThread<T> extends Thread {

    /**
     * Row parse errors
     */
    private List<ErrorLog> errors = new ArrayList<>();

    /**
     * Raw poi sheet
     */
    private Sheet sheet;

    /**
     * Excel parser for sheet data
     */
    private BaseExcelParser<T> parser;

    /**
     * Row data offset
     */
    private int offset = 1;

    /**
     * One time bundle size for parsing and inserting
     */
    private int bundleSize = 200;

    /**
     * Sheet number
     */
    private int sheetNum;

    /**
     * Sheet total row count
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
     * Other sheet numbers which this sheet depended on
     *
     * <p>Sheet thread will start to parse until all its parent sheets have been parsed
     */
    private Set<Integer> parentSheetNum;

    /**
     * Sheet parsing listener
     */
    private SheetCompleteListener listener;

    /**
     * Sheet parsing status
     */
    private SheetStatus.Status status = SheetStatus.Status.PREPARING;

    public BaseSheetImportThread(int sheetNum, BaseExcelParser<T> parser) {
        this(sheetNum, parser, new HashSet<>());
    }

    public BaseSheetImportThread(int sheetNum, BaseExcelParser<T> parser, Set<Integer> parentSheetNum) {
        this.sheetNum = sheetNum;
        this.parser = parser;
        this.parentSheetNum = parentSheetNum;
    }

    /**
     * @param offset row offset to skip
     */
    public void setOffSet(int offset) {
        this.offset = offset;
    }

    /**
     * @param bundleSize row size for on round parsing
     */
    public void setBundleSize(int bundleSize) {
        this.bundleSize = bundleSize;
    }

    /**
     * @return sheet number of parsed sheet
     */
    public int getSheetNum() {
        return sheetNum;
    }

    /**
     * @param sheet raw poi sheet to init with
     */
    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
        totalCnt = sheet.getLastRowNum() + 1 - offset;
    }

    /**
     * @param listener sheet parsing listener to set
     */
    void setListener(SheetCompleteListener listener) {
        this.listener = listener;
    }

    /**
     * @return the depended sheet numbers of this sheet
     */
    Set<Integer> getParentSheetNum() {
        return parentSheetNum;
    }

    /**
     * Get current sheet importing status
     *
     * @return current sheet importing status
     */
    public SheetStatus getStatus() {
        return new SheetStatus(status, sheetNum, sheet == null ? null : sheet.getSheetName(), totalCnt, parseCnt, writeCnt, errors);
    }

    /**
     * Creates bundle data writing task to make data persistent
     *
     * @param data bundle data
     * @return Data writing task
     */
    protected abstract WriteTask<T> createWriteTask(List<T> data);

    @Override
    public void run() {
        status = SheetStatus.Status.IMPORTING;
        int tCount = (int) Math.ceil(totalCnt / ((double) bundleSize));
        FutureTask<Integer> previousTask = null;
        // parse and write data in bulk
        for (int i = 1; i <= tCount; i++) {
            List<RowData<T>> partData = readExcel(sheet, (i - 1) * bundleSize + offset, i * bundleSize + offset);
            if (previousTask != null) {
                // Wait for previous writing task
                waitTask(previousTask);
            }
            // write data asynchronously
            previousTask = new FutureTask<>(createWriteTask(parser.checkBulk(partData, error -> errors.add(error))));
            Thread writeThread = new Thread(previousTask);
            writeThread.start();
            if (i == tCount) {
                // Wait for writing task in last round
                waitTask(previousTask);
            }
        }
        status = SheetStatus.Status.COMPLETE;
        parser.onComplete();
        if (listener != null) {
            listener.onSheetComplete(sheetNum);
        }
    }

    private void waitTask(FutureTask<Integer> task) {
        try {
            writeCnt += task.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private List<RowData<T>> readExcel(Sheet sheet, int startRow, int endRow) {
        int totalRowNum = sheet.getLastRowNum() + 1;
        int rowNum = (totalRowNum < endRow) ? totalRowNum : endRow;
        List<RowData<T>> result = new ArrayList<>();
        for (int i = startRow; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            try {
                result.add(new RowData<>(i, parser.parse(new RowWrapper(row, parser.checkRuleMap()))));
            } catch (ParseException e) {
                errors.add(new ErrorLog(i, e));
            }
            parseCnt = rowNum - offset;
        }
        return result;
    }

    /**
     * Sheet importing listener
     */
    public interface SheetCompleteListener {

        /**
         * Sheet import completed listener
         *
         * @param sheetNum Sheet Number
         */
        void onSheetComplete(Integer sheetNum);
    }

}
