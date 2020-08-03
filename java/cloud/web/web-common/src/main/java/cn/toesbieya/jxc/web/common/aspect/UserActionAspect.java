package cn.toesbieya.jxc.web.common.aspect;

import cn.toesbieya.jxc.api.RecordApi;
import cn.toesbieya.jxc.common.model.entity.RecUserAction;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.common.util.SpringUtil;
import cn.toesbieya.jxc.web.common.annoation.UserAction;
import cn.toesbieya.jxc.web.common.enumeration.UserActionEnum;
import cn.toesbieya.jxc.web.common.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

@Component
@Aspect
@Slf4j
@Order(Integer.MAX_VALUE - 2)
public class UserActionAspect {
    @Reference
    private RecordApi recordApi;

    @Pointcut("@annotation(cn.toesbieya.jxc.web.common.annoation.UserAction)&&execution(cn.toesbieya.jxc.common.model.vo.Result cn.toesbieya.jxc..*.*(..))")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        RecUserAction action = ThreadUtil.getAction();

        if (action == null) {
            return pjp.proceed();
        }

        action.setTime(System.currentTimeMillis());

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Object[] args = pjp.getArgs();
        String[] argNames = signature.getParameterNames();
        UserAction annotation = method.getAnnotation(UserAction.class);

        //优先使用注解表达式
        if (!StringUtils.isEmpty(annotation.value())) {
            String str = (String) SpringUtil.spell(annotation.value(), argNames, args);
            if (!StringUtils.isEmpty(str)) {
                action.setAction(str);
            }
        }

        Result result = (Result) pjp.proceed();

        if (result.isSuccess()) {
            action.setType(UserActionEnum.SUCCESS.getCode());
        }
        else {
            action.setType(UserActionEnum.FAIL.getCode());
            action.setError(result.getMsg());
        }

        recordApi.insertUserAction(action);

        return result;
    }
}
