package cn.toesbieya.jxc.aspect;

import cn.toesbieya.jxc.enumeration.UserActionEnum;
import cn.toesbieya.jxc.exception.JsonResultException;
import cn.toesbieya.jxc.model.entity.RecUserAction;
import cn.toesbieya.jxc.model.vo.Result;
import cn.toesbieya.jxc.service.RecService;
import cn.toesbieya.jxc.util.ThreadUtil;
import cn.toesbieya.jxc.util.Util;
import cn.toesbieya.jxc.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.annotation.Resource;

@Slf4j
@RestControllerAdvice
public class RestExceptionAspect {
    @Resource
    private RecService service;

    //抛出异常时希望返回给前台
    @ExceptionHandler(JsonResultException.class)
    public Result handleJsonResultException(JsonResultException e) {
        recordUserAction(e);
        return Result.fail(e.getMessage());
    }

    //请求方法有误
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return Result.fail("请求方法有误");
    }

    //@RequestParam没有匹配到值
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result handle(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return Result.fail("get参数有误");
    }

    //@RequestBody没有匹配到值
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result handle(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return Result.fail("post参数有误");
    }

    //上传文件过大
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("上传的文件超出限定大小,{}", e.getMessage());
        recordUserAction(e);
        return Result.fail("上传的文件过大");
    }

    //最终捕获
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error(String.format("服务运行异常,访问路径：%s", WebUtil.getRequest().getServletPath()), e);
        recordUserAction(e);
        return Result.fail("服务运行异常");
    }

    private void recordUserAction(Exception e) {
        RecUserAction action = ThreadUtil.getAction();
        if (action != null && !StringUtils.isEmpty(action.getAction())) {
            action.setTime(System.currentTimeMillis());
            action.setError(Util.exception2Str(e));
            action.setType(UserActionEnum.FAIL.getCode());
            service.insertUserAction(action);
        }
        ThreadUtil.clearAll();
    }
}
