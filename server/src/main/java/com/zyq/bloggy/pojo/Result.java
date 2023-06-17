package com.zyq.bloggy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private int code;
    private Map<String, Object> data;
    private String message;

    public static Result ok() {
        return new Result(Code.OK.getCode(), null, "ok");
    }

    public static Result ok(Map<String, Object> data) {
        return new Result(Code.OK.getCode(), data, "ok");
    }

    public static Result ok(String message) {
        return new Result(Code.OK.getCode(), null, message);
    }

    public static Result ok(Map<String, Object> data, String message) {
        return new Result(Code.OK.getCode(), data, message);
    }

    public static Result err() {
        return new Result(Code.ERR.getCode(), null, "err");
    }

    public static Result err(String message) {
        return new Result(Code.ERR.getCode(), null, message);
    }
}

