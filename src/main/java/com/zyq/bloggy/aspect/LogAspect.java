package com.zyq.bloggy.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyq.bloggy.model.pojo.WebLog;
import com.zyq.bloggy.serivce.LogService;
import com.zyq.bloggy.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
@Slf4j
public class LogAspect {
    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    @Autowired
    LogService logService;

    @Pointcut("execution(* com.zyq.bloggy.controller.*.*(..))")
    public void allController() {
    }

    @Pointcut("execution(* com.zyq.bloggy.controller.AdviceController.*(..))")
    public void adviceController() {
    }

    @Pointcut("allController()&&!adviceController()")
    public void point() {
    }

    @Around("point()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Exception {
        WebLog webLog = new WebLog();
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            Long userId = null;
            if (StpUtil.isLogin()) {
                userId = StpUtil.getLoginIdAsLong();
            }
            webLog.setType(LogType.INFO.name());
            webLog.setUserId(userId);
            webLog.setIp(request.getRemoteAddr());
            webLog.setUrl(request.getRequestURL().toString());
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            webLog.setMethod(joinPoint.getTarget().getClass().getName().toString() + "." + methodSignature.getName());
            webLog.setParameter(getParameter(joinPoint.getArgs()));
            long begin = System.currentTimeMillis();
            webLog.setStartTime(new Timestamp(begin));
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();
            webLog.setSpendTime(Math.toIntExact(end - begin));
            webLog.setResult("ok");
            return result;
        } catch (Throwable e) {
            webLog.setType(LogType.ERROR.name());
            webLog.setResult(StringUtils.getExceptionInfo(e));
            throw new RuntimeException(e);
        } finally {
            logService.addLogToQueue(webLog);
        }
    }

    private String getParameter(Object[] parameters) throws JsonProcessingException {
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] instanceof MultipartFile) {
                objects.add(((MultipartFile) parameters[i]).getOriginalFilename());
            } else if (parameters[i] instanceof Serializable) {
                objects.add(parameters[i]);
            }
        }
        return objectMapper.writeValueAsString(objects);
    }

}
