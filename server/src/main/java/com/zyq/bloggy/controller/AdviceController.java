package com.zyq.bloggy.controller;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import com.zyq.bloggy.model.entity.Code;
import com.zyq.bloggy.model.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
}
