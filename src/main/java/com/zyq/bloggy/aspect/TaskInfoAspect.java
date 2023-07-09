package com.zyq.bloggy.aspect;

import com.zyq.bloggy.annotation.TaskInfo;
import com.zyq.bloggy.exception.ServiceException;
import com.zyq.bloggy.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
@Slf4j
public class TaskInfoAspect {
    @Pointcut("@annotation(com.zyq.bloggy.annotation.TaskInfo)")
    public void point() {
    }

    @Around("point()")
    public Object doTask(ProceedingJoinPoint joinPoint) {
        String group = "";
        String value = "";
        String description = "";
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof TaskInfo) {
                TaskInfo taskInfo = (TaskInfo) annotation;
                group = taskInfo.group();
                value = taskInfo.value();
                description = taskInfo.description();
            }
        }
        log.info("{} 定时任务--{}-{}--{}--开始...", TimeUtil.getDateString(new Date()), group, value, description);
        try {
            Object result = joinPoint.proceed();
            log.info("{} 定时任务--{}--{}--结束...", TimeUtil.getDateString(new Date()), group, value);
            return result;
        } catch (Throwable e) {
            throw new ServiceException(String.format("定时任务[%s]-[%s]出错", group, value), e);
        }
    }
}
