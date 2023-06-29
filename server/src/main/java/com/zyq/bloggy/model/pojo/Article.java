package com.zyq.bloggy.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_article", autoResultMap = true)
public class Article {
    @TableId(value = "id", type = IdType.AUTO)

    private Long id;

    private String title;
    private String content;
    private Long author;
    @TableField(value = "tags", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private String[] tags;
    @TableLogic
    private Integer status;
    private Integer views;
    private Integer comments;
    private Integer likeNum;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String description;
}
