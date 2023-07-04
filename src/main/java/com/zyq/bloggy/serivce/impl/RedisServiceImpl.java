package com.zyq.bloggy.serivce.impl;

import com.zyq.bloggy.exception.ServiceException;
import com.zyq.bloggy.model.entity.Status;
import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.serivce.RedisService;
import com.zyq.bloggy.model.vo.AdvertisingVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
//存一个月
public class RedisServiceImpl implements RedisService {
    @Autowired
    RedisTemplate redisTemplate;
    private static final int A_MONTH = 2592000;

    @Override
    @CachePut(cacheNames = "advertising#2592000", key = "#advertisingVo.id")
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

    @Override
    public void like(String key, ThumbsUp thumbs) {
        thumbs.setCreateTime(new Timestamp(System.currentTimeMillis()));
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps(key + ":" + thumbs.getToId());
        String hashKey = thumbs.getFromId() + ":";
        operations.delete(hashKey + Status.INACTIVE.getCode());
        hashKey = hashKey + Status.ACTIVE.getCode();
        boolean flag = operations.putIfAbsent(hashKey, thumbs.getCreateTime());
        if (!flag) {
            throw new ServiceException("请勿频繁操作");
        }
    }

    @Override
    public void cancelLike(String key, ThumbsUp thumbs) {
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps(key + ":" + thumbs.getToId());
        String hashKey = thumbs.getFromId() + ":";
        operations.delete(hashKey + Status.ACTIVE.getCode());
        hashKey = hashKey + Status.INACTIVE.getCode();
        boolean flag = operations.putIfAbsent(hashKey, 0);
        if (!flag) {
            throw new ServiceException("请勿频繁操作");
        }
    }

    @Override
    public void updateLikeCount(String key, Long id, int delta) {
        BoundHashOperations<String, String, Integer> operations = redisTemplate.boundHashOps(key);
        operations.increment(String.valueOf(id), delta);
    }

    @Override
    public Map<Long, List<ThumbsUp>> getLike(String key) {
        HashOperations<String, String, Timestamp> operations = redisTemplate.opsForHash();
        //键：被点赞id，值：点赞信息
        Map<Long, List<ThumbsUp>> listMap = new HashMap<>();
        redisTemplate.keys(key + "*").stream()
                //redis对该数据的存储为{key}:toId
                .forEach(toId -> {
                    String k = String.valueOf(toId);
                    String id = k.substring(k.lastIndexOf(":") + 1);

                    List<ThumbsUp> thumbsUpList = operations.keys((String) toId).stream()
                            //hashkey的格式为fromId:点赞状态
                            .filter(fromId -> Status.ACTIVE.getCode().toString()
                                    .equals(fromId.substring(fromId.length() - 1)))
                            .map(fromId -> {
                                ThumbsUp thumbs = new ThumbsUp();
                                thumbs.setToId(Long.valueOf(id));
                                thumbs.setFromId(Long.valueOf(fromId.substring(0, fromId.length() - 2)));
                                thumbs.setCreateTime(operations.get(String.valueOf(toId), fromId));
                                operations.delete((String) toId, fromId);
                                return thumbs;
                            }).collect(Collectors.toList());
                    listMap.put(Long.valueOf(id), thumbsUpList);
                });
        return listMap;
    /*    HashOperations<String,String,ThumbsUp> operations=redisTemplate.opsForHash();
      return operations.keys(key).stream().filter(k->Status.ACTIVE.getCode().toString().equals(k.substring(k.length()-1))).map(k->{
          log.info("获取{}",key+":"+k);
          ThumbsUp thumbs=operations.get(key,k);
          operations.delete(key,k);
          return  thumbs;
      }).collect(Collectors.toList());*/
    }

    @Override
    public Map<Long, List<ThumbsUp>> getCancelLike(String key) {
        HashOperations<String, String, Timestamp> operations = redisTemplate.opsForHash();
        //键：被点赞id，值：点赞信息
        Map<Long, List<ThumbsUp>> listMap = new HashMap<>();
        redisTemplate.keys(key + "*").stream()
                //redis对该数据的存储为{key}:toId
                .forEach(toId -> {
                    String k = String.valueOf(toId);
                    String id = k.substring(k.lastIndexOf(":") + 1);
                    List<ThumbsUp> thumbsUpList = operations.keys((String) toId).stream()
                            //hashkey的格式为fromId:点赞状态
                            .filter(fromId -> Status.INACTIVE.getCode().toString()
                                    .equals(fromId.substring(fromId.length() - 1)))
                            .map(fromId -> {
                                ThumbsUp thumbs = new ThumbsUp();
                                thumbs.setToId(Long.valueOf(id));
                                thumbs.setFromId(Long.valueOf(fromId.substring(0, fromId.length() - 2)));
                                operations.delete((String) toId, fromId);
                                return thumbs;
                            }).collect(Collectors.toList());
                    listMap.put(Long.valueOf(id), thumbsUpList);
                });
        return listMap;
    }


}
