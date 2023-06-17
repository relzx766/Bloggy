package com.zyq.bloggy.serivce;

import com.zyq.bloggy.vo.AdvertisingVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RedisService {
    AdvertisingVo addAdvertising(AdvertisingVo advertisingVo);

    List<AdvertisingVo> getAdvertising();

    boolean delAdvertising(int id);

    boolean isFileExist(MultipartFile multipartFile);

    void saveFileHash(MultipartFile multipartFile);
}
