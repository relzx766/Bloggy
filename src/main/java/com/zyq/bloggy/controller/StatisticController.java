package com.zyq.bloggy.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.model.entity.Result;
import com.zyq.bloggy.model.vo.AdvertisingVo;
import com.zyq.bloggy.serivce.ArticleService;
import com.zyq.bloggy.serivce.RedisService;
import com.zyq.bloggy.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/statistic")
public class StatisticController {
    @Autowired
    ArticleService articleService;
    @Autowired
    RedisService redisService;

    @GetMapping("/trend")
    public Result getTrend() {
        return new Result().success().put("trends", redisService.getTrend());
    }

    @GetMapping("/ad")
    public Result getAd() {
        Map<String, Object> data = new HashMap<>();
        data.put("ads", redisService.getAdvertising());
        return Result.ok(data);
    }

    @PostMapping("/ad/add")
    @SaCheckRole("ADMIN")
    public Result addAd(@RequestParam("ad") String ad,
                        @RequestParam("image") MultipartFile multipartFile) {

        AdvertisingVo advertisingVo = null;
        try {
            advertisingVo = new ObjectMapper().readValue(ad, AdvertisingVo.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(String.format("广告格式有误[%s]", ad), e);
        }
        advertisingVo.setImage(FileUtil.save(multipartFile));
        advertisingVo = redisService.addAdvertising(advertisingVo);
        Map<String, Object> data = new HashMap<>();
        data.put("ad", advertisingVo);
        return Result.ok(data);
    }

    @PostMapping("/ad/update")
    @SaCheckRole("ADMIN")
    public Result updateAd(@RequestParam("ad") String ad,
                           @RequestParam("image") MultipartFile multipartFile) {
        AdvertisingVo advertisingVo = null;
        try {
            advertisingVo = new ObjectMapper().readValue(ad, AdvertisingVo.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(String.format("广告格式有误[%s]", ad), e);
        }
        if (!FileUtil.isBlank(multipartFile)) {
            advertisingVo.setImage(FileUtil.save(multipartFile));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("ad", redisService.updateAdvertising(advertisingVo)
        );
        return Result.ok(data);
    }

    @PostMapping("/ad/delete")
    @SaCheckRole("ADMIN")
    public Result delAd(@RequestParam("id") Integer id) {
        redisService.delAdvertising(id);
        return Result.ok();
    }

    @GetMapping("/view")
    @SaCheckRole("ADMIN")
    public Result panel() {
        Map<String, Object> data = new HashMap<>();
        data.put("rc", redisService.getPageView());
        data.put("uv", redisService.getUniqueView());
        return Result.ok(data);
    }
}
