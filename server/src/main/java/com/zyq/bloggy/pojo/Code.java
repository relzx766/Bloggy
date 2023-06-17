package com.zyq.bloggy.pojo;

public enum Code {
    OK(2001),
    ERR(2000),
    UNAUTHORIZED(2011),
    SYSTEM_WARNING(5000),
    SYSTEM_ERROR(5010);
    private int code;

    public int getCode() {
        return code;
    }

    Code(int code) {
        this.code = code;
    }
}
