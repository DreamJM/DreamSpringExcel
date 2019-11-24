package com.dream.spring.excel.controller;

import com.dream.spring.excel.annotation.*;
import com.dream.spring.excel.model.Component;
import com.dream.spring.excel.model.PageResult;
import com.dream.spring.excel.model.Result;
import com.dream.spring.excel.model.Test;
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
@ExcelSupport
@Api(tags = "Test Api")
@RestController
public class TestController {

    private long timestamp;

    @ApiOperation("Test")
    @ExcelExport(value = "/api/excel/test", fileName = "test_{timestamp}",
            cache = @Cacheable(timestampField = "date.time", confs = @CacheConf(cacheDir = "hello", condition = "param1 == null", checkUpdateMethod = "isTestUpdated(timestamp)")), annotations = {
            @AnnotationDef(clazz = TestAnnotation.class, members = {@AnnotationMember(name = "value", value = "\"hello\""),
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

    public boolean isTestUpdated(long updateTimestamp) {
        if (timestamp == 0) {
            return true;
        }
        return updateTimestamp < timestamp;
    }
}
