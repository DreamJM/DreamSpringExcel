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

import com.dream.spring.excel.importation.model.Status;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * The main thread used for excel importing
 *
 * @author DreamJM
 */
public class ExcelImportThread extends Thread implements BaseSheetImportThread.SheetCompleteListener {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportThread.class);

    /**
     * Threads for each sheet importing
     */
    private List<BaseSheetImportThread<?>> sheetThreads;

    /**
     * Excel importing complete listener
     */
    private CompleteListener listener;

    /**
     * Key of the thread
     */
    private String id;

    /**
     * Excel file
     */
    private File excel;

    /**
     * Excel Importing Status
     */
    private Status.ExcelStatus status = Status.ExcelStatus.PREPARING;

    /**
     * Excel importing start time
     */
    private Date startTime;

    /**
     * Excel importing end time
     */
    private Date endTime;

    /**
     * Importing suspended sheet thread mapped by sheet number
     */
    private Map<Integer, BaseSheetImportThread<?>> suspendSheetMap;

    /**
     * Sheet dependency map
     */
    private Map<Integer, Set<Integer>> childMap = new HashMap<>();

    /**
     * Importing completed sheet number queue
     */
    private LinkedBlockingQueue<Integer> completeQueue = new LinkedBlockingQueue<>();

    public ExcelImportThread(String id, File excel, List<BaseSheetImportThread<?>> sheetThreads) {
        this.id = id;
        this.excel = excel;
        this.sheetThreads = sheetThreads;
        this.suspendSheetMap = sheetThreads.stream().collect(Collectors.toMap(BaseSheetImportThread::getSheetNum, sheetThread -> sheetThread));
        for (BaseSheetImportThread<?> sheetThread : sheetThreads) {
            for (Integer parentSheetNum : sheetThread.getParentSheetNum()) {
                // Consist sheet dependency map
                if (suspendSheetMap.containsKey(parentSheetNum)) {
                    childMap.computeIfAbsent(parentSheetNum, sheetNum -> new HashSet<>()).add(sheetThread.getSheetNum());
                } else {
                    sheetThread.getParentSheetNum().remove(parentSheetNum);
                }
            }
        }
    }

    public void setCompleteListener(CompleteListener listener) {
        this.listener = listener;
    }

    public Status getStatus() {
        return new Status(status, sheetThreads.stream().map(BaseSheetImportThread::getStatus).collect(Collectors.toList()), startTime, endTime);
    }

    @Override
    public void run() {
        startTime = new Date();
        logger.debug("[{}] excel importing start", excel.getName());
        try (Workbook wb = WorkbookFactory.create(excel)) {
            // Preparing Sheet
            sheetThreads.forEach(sheetThread -> {
                sheetThread.setSheet(wb.getSheetAt(sheetThread.getSheetNum()));
                sheetThread.setListener(this);
            });
            logger.debug("[{}] Sheet preparing completed", excel.getName());
            status = Status.ExcelStatus.IMPORTING;
            if (sheetThreads.size() == 1) {
                // If excel has only one sheet, then the sheet importing task will be executed in the main excel thread
                logger.debug("[{}(Sheet No.{})] Sheet import thread start", excel.getName(), sheetThreads.get(0).getSheetNum());
                sheetThreads.get(0).run();
            } else {
                for (BaseSheetImportThread<?> sheetThread : sheetThreads) {
                    if (sheetThread.getParentSheetNum().size() == 0) {
                        // Start root sheet importing task
                        logger.debug("[{}(Sheet No.{})] Sheet import thread start", excel.getName(), sheetThread.getSheetNum());
                        sheetThread.start();
                    }
                }
                Set<Integer> completeSheets = new HashSet<>();
                while (suspendSheetMap.size() > 0) {
                    Integer sheetNum = completeQueue.poll();
                    completeSheets.add(sheetNum);
                    suspendSheetMap.remove(sheetNum);
                    Set<Integer> childSet = childMap.get(sheetNum);
                    if (childSet != null) {
                        // Judges whether the child sheet can be executed
                        for (Integer childSheetNum : childSet) {
                            BaseSheetImportThread<?> childSheetThread = suspendSheetMap.get(childSheetNum);
                            Set<Integer> childParents = childSheetThread.getParentSheetNum();
                            boolean executable = true;
                            for (Integer childParent : childParents) {
                                if (!completeSheets.contains(childParent)) {
                                    executable = false;
                                    break;
                                }
                            }
                            if (executable) {
                                logger.debug("[{}(Sheet No.{})] Sheet import thread start", excel.getName(),
                                        childSheetThread.getSheetNum());
                                childSheetThread.start();
                            }
                        }
                    }
                }
            }
            endTime = new Date();
            status = Status.ExcelStatus.COMPLETE;
            if (listener != null) {
                listener.onComplete(id, getStatus());
            }
        } catch (Throwable e) {
            logger.error("Error occurred during file importing", e);
            endTime = new Date();
            status = (e instanceof IOException) ? Status.ExcelStatus.IO_ERROR : Status.ExcelStatus.FAIL;
            if (listener != null) {
                listener.onComplete(id, getStatus());
            }
        }
    }

    @Override
    public void onSheetComplete(Integer sheetNum) {
        completeQueue.offer(sheetNum);
    }

    /**
     * Excel Importing Listener
     */
    public interface CompleteListener {

        /**
         * Excel Import completed
         *
         * @param id     Excel Importing Thread ID
         * @param status Excel Importing Status
         */
        void onComplete(String id, Status status);
    }

}
