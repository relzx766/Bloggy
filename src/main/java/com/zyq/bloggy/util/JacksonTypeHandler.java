package com.zyq.bloggy.util;


import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyq.bloggy.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;

import java.util.Objects;

@Slf4j
public class JacksonTypeHandler extends AbstractJsonTypeHandler<Object> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // 未知字段忽略
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 不使用科学计数
        MAPPER.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        // null 值不输出(节省内存)
        MAPPER.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
    }


    @Override
    protected Object parse(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            log.info(String.format("parse failed, jsonStr={}", json, e));
            return null;
        }
    }

    @Override
    protected String toJson(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Object to json string failed!" + obj, e);
        }
    }
}