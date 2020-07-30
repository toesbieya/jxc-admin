package cn.toesbieya.jxc.handler;

import cn.toesbieya.jxc.enumeration.UserActionEnum;
import cn.toesbieya.jxc.exception.JsonResultException;
import cn.toesbieya.jxc.model.entity.RecUserAction;
import cn.toesbieya.jxc.utils.ThreadUtil;
import cn.toesbieya.jxc.utils.Util;
import cn.toesbieya.jxc.service.RecService;
import cn.toesbieya.jxc.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.annotation.Resource;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @Resource
    private RecService recService;

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
        log.error("服务运行异常", e);
        recordUserAction(e);
        return Result.fail("服务运行异常");
    }

    private void recordUserAction(Exception e) {
        RecUserAction action = ThreadUtil.getAction();
        if (action != null && !StringUtils.isEmpty(action.getAction())) {
            action.setError(Util.exception2Str(e));
            recService.insertUserAction(action, UserActionEnum.FAIL);
        }
        ThreadUtil.clearAll();
    }
}
