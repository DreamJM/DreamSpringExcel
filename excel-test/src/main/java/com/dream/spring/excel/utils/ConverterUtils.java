package com.dream.spring.excel.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author DreamJM
 */
public class ConverterUtils {

    public static String formatType(int type) {
        switch (type) {
            case 1:
                return MessageUtils.get("test.type.1");
            case 2:
                return MessageUtils.get("test.type.2");
            default:
                return "";
        }
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

}
