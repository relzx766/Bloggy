package com.zyq.bloggy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
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

    public Result success(String message) {
        this.code = Code.OK.getCode();
        this.message = message;
        this.data = new HashMap<>();
        return this;
    }

    public Result success() {
        this.code = Code.OK.getCode();
        this.message = "ok";
        this.data = new HashMap<>();
        return this;
    }

    public Result error(String message) {
        this.code = Code.ERR.getCode();
        this.message = message;
        this.data = new HashMap<>();
        return this;
    }

    public Result error() {
        this.code = Code.ERR.getCode();
        this.message = "err";
        this.data = new HashMap<>();
        return this;
    }

    public Result put(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}

