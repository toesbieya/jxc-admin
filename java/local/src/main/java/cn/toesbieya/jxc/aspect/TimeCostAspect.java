package cn.toesbieya.jxc.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Aspect
@Slf4j
@Order(1)
public class TimeCostAspect {
    private final ThreadLocal<Instant> instantThreadLocal = new ThreadLocal<>();

    @Pointcut("@annotation(cn.toesbieya.jxc.annoation.TimeCost)")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void before() {
        instantThreadLocal.set(Instant.now());
    }

    @AfterReturning("pointCut()")
    public void afterReturning(JoinPoint point) {
        if (null != instantThreadLocal.get()) {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            log.info("类：{}，方法：{}，耗时：{}毫秒",
                    method.getDeclaringClass().getName(),
                    method.getName(),
                    ChronoUnit.MILLIS.between(instantThreadLocal.get(), Instant.now()));
        }
    }
}
