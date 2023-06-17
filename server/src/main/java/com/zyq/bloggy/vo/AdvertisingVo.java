package com.zyq.bloggy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvertisingVo {
    int id;
    String slogan;
    String url;
    String image;
}
