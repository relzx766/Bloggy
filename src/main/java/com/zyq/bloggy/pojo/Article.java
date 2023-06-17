package com.zyq.bloggy.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.zyq.bloggy.util.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_article")
public class Article {
    @TableId(value = "id", type = IdType.ASSIGN_ID)

    private Long id;

    private String title;
    private String content;
    private Long author;
    @TableField(value = "tags", typeHandler = JacksonTypeHandler.class)
    private String tags;
    @TableLogic
    private Integer status;
    private Integer views;
    private Integer comments;
    private Integer likeNum;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String description;
}
