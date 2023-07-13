package com.zyq.bloggy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleVo {


    private Long id;

    private String title;
    private String content;
    private Integer status;
    private Integer views;
    private Integer comments;
    private Integer likeNum;
    private Timestamp createTime;
    private Timestamp updateTime;
    private UserVo userVo;

    private String[] tags;
    private Boolean isLike;
    private String description;
    private Integer trend;
    private Boolean isSort;
}
