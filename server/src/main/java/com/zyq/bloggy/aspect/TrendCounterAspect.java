package com.zyq.bloggy.aspect;

import com.zyq.bloggy.annotation.TrendCounter;
import com.zyq.bloggy.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(value = 10)
public class TrendCounterAspect {
    private SpelExpressionParser parser = new SpelExpressionParser();
    private DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
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
            key = generateKeyBySPEL(key, joinPoint);
            ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
            if (Objects.isNull(operations.get(key))) {
                operations.set(key, 1, 3, TimeUnit.DAYS);
            } else {
                operations.increment(key, +1);
            }
            return result;
        } catch (BusinessException e) {
            throw new BusinessException("热点数据无效,数据不存在", e);
        } catch (Throwable e) {
            throw new RuntimeException("热点统计切面发生错误", e);
        }
    }

    public String generateKeyBySPEL(String spELString, ProceedingJoinPoint joinPoint) {
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
}