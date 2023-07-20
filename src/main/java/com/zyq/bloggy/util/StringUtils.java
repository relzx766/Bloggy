package com.zyq.bloggy.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    /**
     * springEl表达式生成键
     */
    private static final SpelExpressionParser parser = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

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

    public static String generateKeyBySPEL(String spELString, ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = discoverer.getParameterNames(methodSignature.getMethod());
        Expression expression = parser.parseExpression(spELString);
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        return expression.getValue(context).toString();
    }

    /**
     * 过滤掉markdown文本中的markdown语法部分
     *
     * @param md
     * @return
     */
    public static String filterMd(String md) {
        String regex = "\\*|\\#|\\[|\\]|\\(|\\)|\\!|\\+|\\-|\\.|\\\\|_|\\{|\\}|";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(md);
        return matcher.replaceAll("");
    }

    /**
     * 将文章中所有的图片链接替换为[图片]
     *
     * @param content
     * @return
     */
    public static String replaceImage(String content) {
        String regex = "!\\[.*?\\]\\(.*?\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll("图片");
    }

    public static String cutString(String content, Integer length) {
        if (content.length() > length) {
            return content.substring(0, length);
        } else {
            return content;
        }
    }
}
