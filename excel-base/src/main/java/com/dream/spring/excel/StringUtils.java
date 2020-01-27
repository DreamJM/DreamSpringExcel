package com.dream.spring.excel;

/**
 * @author DreamJM
 */
public class StringUtils {

    public static String valueOf(Object obj) {
        if (obj == null) {
            return "";
        }
        return String.valueOf(obj);
    }

    public static String valueOf(char[] data) {
        return String.valueOf(data);
    }

    public static String valueOf(boolean b) {
        return String.valueOf(b);
    }

    public static String valueOf(char c) {
        return String.valueOf(c);
    }

    public static String valueOf(int i) {
        return String.valueOf(i);
    }

    public static String valueOf(long l) {
        return String.valueOf(l);
    }

    public static String valueOf(float f) {
        return String.valueOf(f);
    }

    public static String valueOf(double d) {
        return String.valueOf(d);
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || "".equals(value);
    }

}
