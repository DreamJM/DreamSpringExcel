package com.dream.spring.excel.test.model;

import com.dream.spring.excel.annotation.SheetWrapper;

import java.util.List;

public class PageResult<T> {

    private Integer pageNum;

    private Integer pageSize;

    private Integer pages;

    private long total;

    @SheetWrapper
    private List<T> values;

    public PageResult(List<T> values) {
        this.values = values;
        total = values.size();
    }

    public PageResult(Integer pageNum, Integer pageSize, Integer pages, Long total, List<T> values) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
        this.total = total;
        this.values = values;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getPages() {
        return pages;
    }

    public long getTotal() {
        return total;
    }

    public List<T> getValues() {
        return values;
    }
}
