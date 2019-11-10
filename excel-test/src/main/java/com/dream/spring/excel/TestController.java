package com.dream.spring.excel;

import com.dream.spring.excel.annotation.ExcelExport;
import com.dream.spring.excel.annotation.ExcelSupport;
import com.dream.spring.excel.annotation.ParamIgnore;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DreamJM
 */
@ExcelSupport
@RestController
public class TestController {

    @RequestMapping("hello")
    @ExcelExport(value = "/api/hello")
    public Result<PageResult<Test>> test(@RequestParam String hello, @PathVariable String test, @RequestBody String userID,
                                         @ParamIgnore String ignore) {
        List<Test> tests = new ArrayList<>();
        tests.add(new Test("hello1", "world1", "2019-10-21"));
        tests.add(new Test("hello2", "world2", "2019-10-22"));
        tests.add(new Test("hello3", "world3", "2019-10-23"));
        tests.add(new Test("hello4", "world4", "2019-10-24"));
        tests.add(new Test("hello5", "world5", "2019-10-25"));
        PageResult<Test> page = new PageResult<>(tests);
        return new Result<>(page);
    }

}
