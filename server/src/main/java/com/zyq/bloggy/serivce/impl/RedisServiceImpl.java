package com.zyq.bloggy.serivce.impl;

import com.zyq.bloggy.exception.ServiceException;
import com.zyq.bloggy.serivce.RedisService;
import com.zyq.bloggy.vo.AdvertisingVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
//存一个月
@CacheConfig(cacheNames = "advertising#2592000")
public class RedisServiceImpl implements RedisService {
    @Autowired
    RedisTemplate redisTemplate;
    private static final int A_MONTH = 2592000;

    @Override
    @CachePut(key = "#advertisingVo.id")
    public AdvertisingVo addAdvertising(AdvertisingVo advertisingVo) {
        String id = String.valueOf(System.currentTimeMillis());
        id = id.substring(id.length() - 4);
        advertisingVo.setId(Integer.parseInt(id));
        return advertisingVo;
    }

    @Override
    public List<AdvertisingVo> getAdvertising() {
        return (List<AdvertisingVo>) redisTemplate.keys("advertising*").stream().map(key ->
                redisTemplate.opsForValue().get(key)
        ).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean delAdvertising(int id) {
        return true;
    }

    @Override
    public boolean isFileExist(MultipartFile multipartFile) {
        try {
            String hashCode = DigestUtils.md5Hex(multipartFile.getInputStream());
            Object obj = redisTemplate.opsForValue().get(hashCode);
            return Objects.nonNull(obj);
        } catch (IOException e) {
            throw new ServiceException("读取文件hash出错", e);
        }
    }

    @Override
    public void saveFileHash(MultipartFile multipartFile) {
        try {
            String hashCode = DigestUtils.md5Hex(multipartFile.getInputStream());
            redisTemplate.opsForValue().set(hashCode, 1);
        } catch (IOException e) {
            throw new ServiceException("存储文件hash出错", e);
        }
    }

}
