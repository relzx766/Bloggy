package com.zyq.bloggy.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String getExceptionInfo(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer = stringWriter.getBuffer();
        return buffer.toString();
    }

    public static boolean isEmail(String str) {
        str = str.trim();
        String regex = "^[a-zA-Z0-9_]+@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 替查找markdown里第一个图片
     *
     * @param content
     * @return 存在则返回图片链接，不存在返回null
     */
    public static String getImageOfMarkdown(String content) {
        String regex = "\\!\\[.*\\]\\(.+\\)";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }
}
