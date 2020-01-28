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

import com.dream.spring.excel.StringUtils;
import com.dream.spring.excel.importation.exception.ParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Row data wrapper bound with {@link CheckRule CheckRule}'s map
 *
 * @author DreamJM
 */
public class RowWrapper {

    private static final String AFFIX_POINT_ZERO = ".0";

    /**
     * Raw row poi object
     */
    private Row row;

    /**
     * Rules mapped by column number
     */
    private Map<Integer, CheckRule> ruleMap;

    public RowWrapper(Row row, Map<Integer, CheckRule> ruleMap) {
        this.row = row;
        this.ruleMap = ruleMap;
    }

    /**
     * @return raw row poi object
     */
    public Row getRow() {
        return row;
    }

    /**
     * Get cell value of the specified column number
     *
     * @param column Column Number
     * @return Cell Value
     * @throws ParseException Cell value parse exception
     */
    public String getCellValue(int column) throws ParseException {
        String value = parseValue(row.getCell(column));
        if (ruleMap != null && ruleMap.containsKey(column)) {
            CheckRule rule = ruleMap.get(column);
            if (rule.isRequired() && StringUtils.isNullOrEmpty(value)) {
                throw new ParseException(ParseException.Reason.Empty, rule.getName());
            }
            if (StringUtils.isNullOrEmpty(value)) {
                return rule.getDefaultValue();
            }
            if (rule.getLength() > 0 && value.length() > rule.getLength()) {
                throw new ParseException(ParseException.Reason.LengthExceeded, rule.getName());
            }
            if (!StringUtils.isNullOrEmpty(rule.getRegex())) {
                Pattern pattern = Pattern.compile(rule.getRegex());
                Matcher matcher = pattern.matcher(value);
                if (!matcher.matches()) {
                    throw new ParseException(ParseException.Reason.Invalid, rule.getName(), rule.getFormatHint());
                }
                matcher = pattern.matcher(value);
                if (rule.getRegexGroupNum() != null && matcher.find()) {
                    String matchedValue = matcher.group(rule.getRegexGroupNum());
                    if (rule.getValidValue() != null) {
                        if (!rule.getValidValue().contains(matchedValue)) {
                            throw new ParseException(ParseException.Reason.RangeExceeded, rule.getName(), rule.getFormatHint());
                        }
                    }
                    value = matchedValue;
                }
            }
        }
        return value;
    }

    private static String parseValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                DecimalFormat df = new DecimalFormat("####################.###########");
                String number = df.format(cell.getNumericCellValue());
                if (number.endsWith(AFFIX_POINT_ZERO)) {
                    number = number.replaceAll(AFFIX_POINT_ZERO, "");
                }
                return number;
            case STRING:
                return cell.getStringCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
