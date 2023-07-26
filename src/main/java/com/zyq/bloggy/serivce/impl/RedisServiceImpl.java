package com.zyq.bloggy.serivce.impl;

import com.zyq.bloggy.aspect.StatisticsAspect;
import com.zyq.bloggy.exception.ServiceException;
import com.zyq.bloggy.model.entity.Status;
import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.model.vo.AdvertisingVo;
import com.zyq.bloggy.serivce.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
//存一个月
public class RedisServiceImpl implements RedisService {
    private static final String KEY_AD = "advertising";
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public AdvertisingVo addAdvertising(AdvertisingVo advertisingVo) {
        String id = String.valueOf(System.currentTimeMillis());
        id = id.substring(id.length() - 4);
        String key = KEY_AD + ":" + id;
        advertisingVo.setId(Integer.parseInt(id));
        advertisingVo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        redisTemplate.opsForValue().set(key, advertisingVo, Duration.ofDays(advertisingVo.getExpire()));
        return advertisingVo;
    }

    @Override
    public List<AdvertisingVo> getAdvertising() {
        return (List<AdvertisingVo>) redisTemplate.keys(KEY_AD + "*").stream().map(key ->
                redisTemplate.opsForValue().get(key)
        ).collect(Collectors.toList());
    }

    @Override
    public AdvertisingVo updateAdvertising(AdvertisingVo advertisingVo) {
        String key = KEY_AD + ":" + advertisingVo.getId();
        advertisingVo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        redisTemplate.opsForValue().set(key, advertisingVo, Duration.ofDays(advertisingVo.getExpire()));
        return advertisingVo;
    }

    @Override
    @CacheEvict(cacheNames = KEY_AD, key = "#id")
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

    @Override
    public Object getPageView() {
        String cacheKey = StatisticsAspect.KEY_REQUEST_COUNT;
        BoundHashOperations<String, String, Integer> operations = redisTemplate.boundHashOps(cacheKey);
        return groupPvOrUv(operations.entries());
    }

    @Override
    public Object getUniqueView() {
        Map<String, Integer> uv = new HashMap<>();
        HashOperations operations = redisTemplate.opsForHash();
        redisTemplate.keys(StatisticsAspect.KEY_UNIQUE_VISITOR + "*").forEach((key -> {
            String k = key.toString();
            k = k.substring(StatisticsAspect.KEY_UNIQUE_VISITOR.length() + 1);
            uv.put(k, Math.toIntExact(operations.size(key)));
        }));
        return groupPvOrUv(uv);
    }

    @Override
    @Async
    public void incrementTrend(String content) {
        BoundHashOperations<String, String, Integer> operations = redisTemplate.boundHashOps("trend");
        operations.increment(content, 1);
    }

    @Override
    @Async
    public void incrementTrend(String[] content) {
        BoundHashOperations<String, String, Integer> operations = redisTemplate.boundHashOps("trend");
        for (int i = 0; i < content.length; i++) {
            operations.increment(content[i], 1);
        }
    }

    @Override
    public Map<String, Integer> getTrend() {
        BoundHashOperations<String, String, Integer> operations = redisTemplate.boundHashOps("trend");
        Map<String, Integer> trends = new HashMap<>();
        return operations.keys().stream().map(key -> Map.entry(key, operations.get(key)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new
                ));
    }

    /**
     * 用于对rc和uv分组，rc的存储格式为年-月-日：数量，uv经过运算后同上
     *
     * @param data
     * @return
     */
    private Map<Integer, LinkedHashMap<String, List<Map.Entry<String, Integer>>>> groupPvOrUv(Map<String, Integer> data) {
        SimpleDateFormat dateFormat = StatisticsAspect.dateFormat;
        DecimalFormat decimalFormat = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        return data.entrySet().stream().collect(
                //先将数据按照年份分组
                Collectors.groupingBy(e -> {
                            try {
                                Date date = dateFormat.parse(e.getKey());
                                calendar.setTime(date);
                                return calendar.get(Calendar.YEAR);
                            } catch (ParseException ex) {
                                throw new RuntimeException(ex);
                            }
                        },
                        //经过上面分组后，数据格式为{2023:[2023-07-19]...}
                        Collectors.toMap(
                                //将数据转化为 "07": [{"2023-07-19": 3 }]这样的格式
                                e -> {
                                    Date date;
                                    try {
                                        date = dateFormat.parse(e.getKey());
                                    } catch (ParseException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                    calendar.setTime(date);
                                    return decimalFormat.format(calendar.get(Calendar.MONTH) + 1);
                                },
                                //这样就获取了以月份为键的Map了
                                //Collections.singletonList会创建一个只包含一条数据的list，此时经过这里的数据为[{"07": [{"19": 3 }]}]
                                e -> {
                                    //因为calendar非线程安全，所以这里还是要重新设置一下date
                                    Date date;
                                    try {
                                        date = dateFormat.parse(e.getKey());
                                    } catch (ParseException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                    calendar.setTime(date);
                                    return Collections.singletonList(Map.entry(
                                            decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH))
                                            , e.getValue()));
                                }
                                ,
                                //当键，即月份相同时，将数据合并，这里的数据来自于上面的singletonList
                                (oldEntity, newEntity) -> {
                                    List<Map.Entry<String, Integer>> list = new ArrayList<>(oldEntity);
                                    list.addAll(newEntity);
                                    return list;
                                },
                                //最后将数据存入这里，使用LinkedHashMap是为了保证有序
                                LinkedHashMap::new
                        )
                ));
    }


}
