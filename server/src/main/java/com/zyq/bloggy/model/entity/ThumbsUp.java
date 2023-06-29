package com.zyq.bloggy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThumbsUp {
    //用户id
    private Long fromId;
    //点赞的文章、评论id
    private Long toId;
    private Timestamp createTime;
}
