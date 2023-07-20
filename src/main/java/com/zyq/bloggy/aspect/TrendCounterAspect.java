package com.zyq.bloggy.aspect;

import com.zyq.bloggy.annotation.TrendCounter;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.util.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 该切面可统计热度，同时也可以计算缓存权重
 */

@Aspect
@Component
@Order(value = 10)
public class TrendCounterAspect {

    @Autowired
    RedisTemplate redisTemplate;

    @Pointcut("@annotation(com.zyq.bloggy.annotation.TrendCounter)")
    public void point() {
    }

    @Around(value = "point()")
    public Object aroundTrend(ProceedingJoinPoint joinPoint) {
        try {
            Object result = joinPoint.proceed();
            Class<?> clazz = joinPoint.getTarget().getClass();
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method targetMethod = clazz.getDeclaredMethod(
                    methodSignature.getName(),
                    methodSignature.getParameterTypes()
            );
            Annotation[] annotations = targetMethod.getAnnotations();
            String key = "TREND_EXCEPTION_KEY";
            for (Annotation anno : annotations) {
                if (anno instanceof TrendCounter) {
                    key = ((TrendCounter) anno).key();
                    break;
                }
            }
            key = StringUtils.generateKeyBySPEL(key, joinPoint);
            ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
            System.out.println(operations.get(key));
            if (Objects.isNull(operations.get(key))) {
                operations.set(key, 100, 7, TimeUnit.DAYS);
            } else {
                operations.increment(key, 1);
            }
            return result;
        } catch (BusinessException e) {
            throw new BusinessException("热点数据无效,数据不存在", e);
        } catch (Throwable e) {
            throw new RuntimeException("热点统计切面发生错误", e);
        }
    }


}