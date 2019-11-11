package com.dream.spring.excel.controller;

import com.dream.spring.excel.annotation.ExcelExport;
import com.dream.spring.excel.annotation.ExcelSupport;
import com.dream.spring.excel.annotation.ParamIgnore;
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
@Api(tags = "测试接口")
@RestController
public class TestController {

    @ApiOperation("测试")
    @ExcelExport(value = "/api/excel/test")
    @GetMapping("/api/test")
    public Result<PageResult<Test>> test(@RequestParam String param1, @ParamIgnore("-1") @RequestParam int type,
                                         @ParamIgnore @RequestParam(required = false) Integer pageNum,
                                         @ParamIgnore @RequestParam(required = false) Integer pageSize) {
        List<Test> tests = new ArrayList<>();
        tests.add(new Test("hello1", "world1", 1, new Date(), new Component("component1")));
        tests.add(new Test("hello2", "world2", 2, new Date(System.currentTimeMillis() + 20000L), new Component("component2")));
        tests.add(new Test("hello3", "world3", 3, new Date(System.currentTimeMillis() + 40000L), new Component("component3")));
        tests.add(new Test("hello4", "world4", 4, new Date(System.currentTimeMillis() + 60000L), new Component("component4")));
        tests.add(new Test("hello5", "world5", 5, new Date(System.currentTimeMillis() + 80000L), new Component("component5")));
        PageResult<Test> page = new PageResult<>(tests);
        return new Result<>(page);
    }
}
