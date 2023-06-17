package com.zyq.bloggy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyq.bloggy.exception.ServiceException;
import com.zyq.bloggy.pojo.Result;
import com.zyq.bloggy.serivce.ArticleService;
import com.zyq.bloggy.serivce.RedisService;
import com.zyq.bloggy.util.FileUtil;
import com.zyq.bloggy.vo.AdvertisingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/trend")
public class TrendController {
    @Autowired
    ArticleService articleService;
    @Autowired
    RedisService redisService;

    @GetMapping("/article")
    public Result getTrend() {
        Map<String, Object> data = new HashMap<>();
        data.put("articles", articleService.getTrend());
        return Result.ok(data);
    }

    @GetMapping("/ad")
    public Result getAd() {
        Map<String, Object> data = new HashMap<>();
        data.put("ads", redisService.getAdvertising());
        return Result.ok(data);
    }

    @PostMapping("/ad/add")
    public Result addAd(@RequestParam("ad") String ad,
                        @RequestParam("image") MultipartFile multipartFile) {

        AdvertisingVo advertisingVo = null;
        try {
            advertisingVo = new ObjectMapper().readValue(ad, AdvertisingVo.class);
        } catch (JsonProcessingException e) {
            throw new ServiceException(String.format("json转换失败{}", ad), e);
        }
        advertisingVo.setImage(FileUtil.save(multipartFile));
        redisService.addAdvertising(advertisingVo);
        return Result.ok();
    }
}
