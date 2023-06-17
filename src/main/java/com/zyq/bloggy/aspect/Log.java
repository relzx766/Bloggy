package com.zyq.bloggy.aspect;

import cn.dev33.satoken.stp.StpUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Aspect
@Component
public class Log {
    @Pointcut("@annotation(com.zyq.bloggy.annotation.Log)")
    public void point() {
    }

    @After("point()")
    public void doLog(JoinPoint joinPoint) {
        Long userId = (Long) StpUtil.getLoginIdDefaultNull();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        String time = new SimpleDateFormat("yyyy:MM:dd-hh:mm:ss").format(new Date());
        System.out.format("%s于%s调用了%s.%s", userId, time, className, methodName);
    }
}
