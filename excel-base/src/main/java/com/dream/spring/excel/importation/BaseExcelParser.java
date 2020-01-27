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
import com.dream.spring.excel.importation.model.CheckRule;
import com.dream.spring.excel.importation.model.ErrorLog;
import com.dream.spring.excel.importation.model.RowData;
import com.dream.spring.excel.importation.model.RowWrapper;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Base class of excel sheet parser
 *
 * <p>Each sheet should have one Excel Parser Object to parse and check the form data
 *
 * @param <T> Row Data Type
 * @author DreamJM
 */
public abstract class BaseExcelParser<T> {

    /**
     * Parse the excel row data to the specified object
     *
     * @param row Row Data Wrapper
     * @return Row Data Object
     * @throws ParseException Parse Error Exception
     */
    public abstract T parse(RowWrapper row) throws ParseException;

    /**
     * Check the row data in bulk(specified in {@link BaseSheetImportThread})
     *
     * <p>In some situation, you may check the data through SQL or RPC. Check one by one in {@link #parse(RowWrapper)} is very costly, so
     * you may need this method to check in bulk
     *
     * @param bulkData    Row Data in bulk
     * @param errorLogger Error Logger that collect row errors
     * @return Valid Rows data
     */
    public List<T> checkBulk(List<RowData<T>> bulkData, Consumer<ErrorLog> errorLogger) {
        return bulkData.stream().map(RowData::getData).collect(Collectors.toList());
    }

    /**
     * Get the rules mapped by column number
     *
     * @return the rules mapped by column number
     */
    public Map<Integer, CheckRule> checkRuleMap() {
        return null;
    }

    /**
     * Called when sheet parse completed
     */
    public void onComplete() {
    }
}
