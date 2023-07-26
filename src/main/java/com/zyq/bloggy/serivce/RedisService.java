package com.zyq.bloggy.serivce;

import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.model.vo.AdvertisingVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface RedisService {
    AdvertisingVo addAdvertising(AdvertisingVo advertisingVo);

    List<AdvertisingVo> getAdvertising();

    AdvertisingVo updateAdvertising(AdvertisingVo advertisingVo);

    boolean delAdvertising(int id);

    boolean isFileExist(MultipartFile multipartFile);

    void saveFileHash(MultipartFile multipartFile);

    void like(String key, ThumbsUp thumbs);

    void cancelLike(String key, ThumbsUp thumbs);

    void updateLikeCount(String key, Long id, int delta);

    //返回一个由被点赞id和点赞信息的map
    Map<Long, List<ThumbsUp>> getLike(String key);

    //返回一个由被取消点赞id和点赞信息的map
    Map<Long, List<ThumbsUp>> getCancelLike(String key);

    Object getPageView();

    Object getUniqueView();

    void incrementTrend(String content);

    void incrementTrend(String[] content);

    Map<String, Integer> getTrend();


}
