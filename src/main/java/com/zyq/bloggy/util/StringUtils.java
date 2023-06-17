package com.zyq.bloggy.util;

public class StringUtils {
    public static Boolean isBlank(String s) {
        if (s == null) {
            return true;
        }
        if (s.isEmpty()) {
            return true;
        }
        return false;
    }

    public static Boolean isAnyBlank(String... s) {
        for (String str : s) {
            if (isBlank(str)) {
                return true;
            }
        }
        return false;
    }
}
