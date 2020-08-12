package cn.toesbieya.jxc.web.common.aspect;

import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.common.util.RedisUtil;
import cn.toesbieya.jxc.common.util.SpringUtil;
import cn.toesbieya.jxc.web.common.annoation.Lock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;

@Component
@Aspect
@Slf4j
@Order(Integer.MAX_VALUE - 1)
public class LockAspect {
    private final ThreadLocal<ArrayList<RedisUtil.Locker>> lockHelperThreadLocal = new ThreadLocal<>();

    @Pointcut("@annotation(cn.toesbieya.jxc.web.common.annoation.Lock)&&execution(cn.toesbieya.jxc.common.model.vo.R cn.toesbieya.jxc..*.*(..))")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String[] values = method.getAnnotation(Lock.class).value();

        if (values.length <= 0) {
            return point.proceed();
        }

        ArrayList<RedisUtil.Locker> locks = new ArrayList<>();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = point.getArgs();

        for (String v : values) {
            String lockKey = (String) SpringUtil.spell(v, parameterNames, args);

            //跳过空值
            if (StringUtils.isEmpty(lockKey)) continue;

            RedisUtil.Locker lockHelper = new RedisUtil.Locker(lockKey);

            if (!lockHelper.lock()) {
                locks.forEach(RedisUtil.Locker::close);
                return R.fail("操作失败，请刷新后重试");
            }

            locks.add(lockHelper);
        }

        lockHelperThreadLocal.set(locks);
        return point.proceed();
    }

    @AfterReturning("pointCut()")
    public void afterReturning() {
        unlock();
    }

    @AfterThrowing("pointCut()")
    public void afterThrowing() {
        unlock();
    }

    private void unlock() {
        ArrayList<RedisUtil.Locker> locks = lockHelperThreadLocal.get();
        if (!CollectionUtils.isEmpty(locks)) {
            locks.forEach(RedisUtil.Locker::close);
            lockHelperThreadLocal.remove();
        }
    }
}
