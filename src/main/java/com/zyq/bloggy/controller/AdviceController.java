package com.zyq.bloggy.controller;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.exception.ServiceException;
import com.zyq.bloggy.model.entity.Code;
import com.zyq.bloggy.model.entity.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class AdviceController {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(SaTokenException.class)
    public Result authorizeExceptionHandler(SaTokenException e) {
        if (e instanceof NotLoginException) {
            return new Result(Code.UNAUTHORIZED.getCode(), null, "请先登录");
        } else if (e instanceof NotRoleException) {
            return new Result(Code.INSUFFICIENT_PRIVILEGES.getCode(), null, "你没有该权限");
        }
        return Result.err(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Throwable.class)
    public Result ExceptionHandler(Throwable e) {
        e = e.getCause();
        if (e instanceof BusinessException) {
            return Result.err(e.getMessage());
        } else if (e instanceof ServiceException) {
            return Result.err("服务器异常");
        } else {
            return Result.err("服务器错误");
        }
    }
}
