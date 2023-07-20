package com.zyq.bloggy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvertisingVo {
    int id;
    String slogan;
    String url;
    String image;
    Timestamp createTime;
    int expire;

}
