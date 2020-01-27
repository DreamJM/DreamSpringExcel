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

import com.dream.spring.excel.annotation.*;
import com.dream.spring.excel.test.annotation.ChildValue;
import com.dream.spring.excel.test.annotation.TestAnnotation;
import com.dream.spring.excel.test.model.Component;
import com.dream.spring.excel.test.model.PageResult;
import com.dream.spring.excel.test.model.Result;
import com.dream.spring.excel.test.model.Test;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author DreamJM
 */
@ExcelSupport("com.dream.spring.excel.test.controller.excel.ExcelController")
@Api(tags = "Test Api")
@RestController
public class TestController {

    private long timestamp;

    @ApiOperation("Test")
    @ExcelExport(value = "/api/excel/test",
            annotations = {@AnnotationDef(clazz = TestAnnotation.class, members = {@AnnotationMember(name = "value", value = "\"hello\""),
                    @AnnotationMember(name = "children", value = "value=\"child\"", annotation = ChildValue.class)})})
    @GetMapping("/api/test")
    public Result<PageResult<Test>> test(@RequestParam(required = false) String param1, @ParamIgnore("-1") @RequestParam int type,
                                         @ParamIgnore @RequestParam(required = false) Integer pageNum,
                                         @ParamIgnore @RequestParam(required = false) Integer pageSize) {
        Date date = new Date();
        timestamp = date.getTime();
        List<Test> tests = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            tests.add(new Test("hello" + i, "world" + i, (i - 1) % 2 + 1, date, new Component("child" + i, "childValue" + i)));
        }
        PageResult<Test> page = new PageResult<>(tests);
        return new Result<>(page);
    }

    @ApiOperation("Cache Test")
    @ExcelExport(value = "/api/excel/cache/test", fileName = "cache_{timestamp}",
            caches = @Cacheable(cacheDir = "hello", condition = "param1 == null", checkUpdateMethod = "isTestUpdated(timestamp)", timestampMethod = "getTimestamp(sheet)"))
    @GetMapping("/api/cache/test")
    public Result<PageResult<Test>> cacheTest(@RequestParam(required = false) String param1,
                                              @ParamIgnore @RequestParam(required = false) Integer pageNum,
                                              @ParamIgnore @RequestParam(required = false) Integer pageSize) {
        Date date = new Date();
        timestamp = date.getTime();
        List<Test> tests = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            tests.add(new Test("hello" + i, "cache" + i, (i - 1) % 2 + 1, date, new Component("cacheChild" + i, "cacheChildValue" + i)));
        }
        PageResult<Test> page = new PageResult<>(tests);
        return new Result<>(page);
    }

    public long getTimestamp(List<Test> sheet) {
        long maxTimestamp = 0;
        for (Test test : sheet) {
            if (test.getDate() != null && test.getDate().getTime() > maxTimestamp) {
                maxTimestamp = test.getDate().getTime();
            }
        }
        return maxTimestamp;
    }

    public boolean isTestUpdated(long updateTimestamp) {
        if (timestamp == 0) {
            return true;
        }
        return updateTimestamp < timestamp;
    }
}
