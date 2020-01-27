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

package com.dream.spring.excel.test.controller;

import com.dream.spring.excel.FileUtils;
import com.dream.spring.excel.importation.BaseExcelParser;
import com.dream.spring.excel.importation.BaseSheetImportThread;
import com.dream.spring.excel.importation.ExcelImportThread;
import com.dream.spring.excel.importation.WriteTask;
import com.dream.spring.excel.importation.exception.ParseException;
import com.dream.spring.excel.importation.model.CheckRule;
import com.dream.spring.excel.importation.model.ErrorLog;
import com.dream.spring.excel.importation.model.RowData;
import com.dream.spring.excel.importation.model.RowWrapper;
import com.dream.spring.excel.test.model.Component;
import com.dream.spring.excel.test.model.Test;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author DreamJM
 */
@Api(tags = "Excel Import Api")
@RestController
public class ImportController {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @PostMapping("/api/excel/import")
    public String excelImport(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "Upload Error!";
        }
        File targetFile = new File(FileUtils.cacheDir("upload"), file.getOriginalFilename());
        file.transferTo(targetFile.getAbsoluteFile());
        ExcelImportThread thread =
                new ExcelImportThread(UUID.randomUUID().toString(), targetFile, Collections.singletonList(new BaseSheetImportThread<Test>(0,
                        new BaseExcelParser<Test>() {
                            @Override
                            public Test parse(RowWrapper row) throws ParseException {
                                String name = row.getCellValue(1);
                                String value = row.getCellValue(2);
                                String type = row.getCellValue(3);
                                String date = row.getCellValue(4);
                                String childName = row.getCellValue(5);
                                String childValue = row.getCellValue(6);
                                try {
                                    return new Test(name, value, Integer.parseInt(type), sdf.parse(date),
                                            new Component(childName, childValue));
                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                    throw new ParseException(ParseException.Reason.Invalid, "date", "date format error!");
                                }
                            }

                            @Override
                            public List<Test> checkBulk(List<RowData<Test>> bulkData, Consumer<ErrorLog> errorLogger) {
                                return super.checkBulk(bulkData, errorLogger);
                            }

                            @Override
                            public Map<Integer, CheckRule> checkRuleMap() {
                                return new HashMap<Integer, CheckRule>() {
                                    {
                                        put(1, CheckRule.builder("name").setRequired(true).setLength(12).build());
                                        put(2, CheckRule.builder("value").setRequired(true).setLength(12).build());
                                        put(3, CheckRule.builder("type").setRequired(true).setRegex("[\\S]*([\\d]+?)").setRegexGroupNum(1)
                                                .setValidValue(Sets.newHashSet("1", "2")).build());
                                        put(4, CheckRule.builder("date").setRequired(true).setRegex("[\\d]{4}-[\\d]{2}-[\\d]{2}").build());
                                        put(5, CheckRule.builder("childName").setLength(12).build());
                                        put(6, CheckRule.builder("childValue").setLength(12).build());
                                    }
                                };
                            }

                            @Override
                            public void onComplete() {
                                super.onComplete();
                            }
                        }) {
                    {
                        setOffSet(2);
                    }

                    @Override
                    protected WriteTask<Test> createWriteTask(List<Test> data) {
                        return new WriteTask<Test>(data) {
                            @Override
                            protected Integer write(List<Test> data) {
                                return data.size();
                            }
                        };
                    }
                }));
        // Just for test
        thread.run();
        return thread.getStatus().toString();
    }

}
