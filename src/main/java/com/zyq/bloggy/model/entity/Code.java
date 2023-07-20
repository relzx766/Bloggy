package com.zyq.bloggy.model.entity;

public enum Code {
    OK(2001),
    ERR(2000),
    //未登录
    UNAUTHORIZED(2011),
    SYSTEM_WARNING(5000),
    //权限不足
    INSUFFICIENT_PRIVILEGES(2021),
    SYSTEM_ERROR(5010);
    private int code;

    Code(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
