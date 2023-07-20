package com.zyq.bloggy.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.zyq.bloggy.annotation.Statistics;
import com.zyq.bloggy.exception.ServiceException;
import com.zyq.bloggy.util.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;

@Aspect
@Component
@Order(10)
public class StatisticsAspect {
    public static final String DEFAULT_KEY_HEAD = "statistics";
    public static final String KEY_REQUEST_COUNT = "statistics:requestCount";
    public static final String KEY_TOTAL = "total";
    public static final String KEY_UNIQUE_VISITOR = "statistics:uniqueVisitor";
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    RedisTemplate redisTemplate;

    @Pointcut("execution(* com.zyq.bloggy.controller.*.*(..))")
    public void viewCut() {
    }

    @Pointcut("@annotation(com.zyq.bloggy.annotation.Statistics)")
    public void annoCut() {
    }

    @Before("viewCut()")
    public void before() {
        pageViewCounter();
        uniqueVisitorCounter();
    }

    @Around("annoCut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Annotation[] annotations = methodSignature.getMethod().getAnnotations();
        Statistics statistics = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof Statistics) {
                statistics = (Statistics) annotation;
                break;
            }
        }
        try {
            Object result = joinPoint.proceed();
            StringBuffer key = new StringBuffer();
            key.append(DEFAULT_KEY_HEAD).append(":");
            if (!StringUtils.isBlank(statistics.cacheName().trim())) {
                key.append(statistics.cacheName()).append(":");
            }
            key.append(StringUtils.generateKeyBySPEL(statistics.key(), joinPoint));
            ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
            if (statistics.timeout() == -1) {
                operations.setIfAbsent(key.toString(), 0);
            } else {
                operations.setIfAbsent(key.toString(), 0, statistics.timeout(), statistics.util());
            }
            operations.increment(key.toString(), statistics.delta());
            return result;
        } catch (Throwable throwable) {
            throw new ServiceException(String.format("统计{%s}{%s}时发生异常", statistics.cacheName(), statistics.key()));
        }

    }

    /**
     * 统计rc
     */

    private void pageViewCounter() {
        BoundHashOperations<String, String, Long> operations = redisTemplate.boundHashOps(KEY_REQUEST_COUNT);
        String date = dateFormat.format(System.currentTimeMillis());
        operations.increment(date, 1);
    }

    /**
     * 统计QV，存储的格式为如下:
     * 键为统一的键头:年月日
     * 值为用户id的集合
     * 键头{
     * 年月日{
     * 用户id集合
     * }
     * }
     */

    private void uniqueVisitorCounter() {
        if (StpUtil.isLogin()) {
            long userId = StpUtil.getLoginIdAsLong();
            String key = KEY_UNIQUE_VISITOR + ":" + dateFormat.format(System.currentTimeMillis());
            BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps(key);
            operations.putIfAbsent(String.valueOf(userId), 1);
        }
    }
}
