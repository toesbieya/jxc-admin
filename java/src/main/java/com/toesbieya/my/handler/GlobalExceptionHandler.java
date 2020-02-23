package com.toesbieya.my.handler;

import com.toesbieya.my.enumeration.UserActionEnum;
import com.toesbieya.my.exception.JsonResultException;
import com.toesbieya.my.model.entity.RecUserAction;
import com.toesbieya.my.service.RecService;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.ThreadUtil;
import com.toesbieya.my.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
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
