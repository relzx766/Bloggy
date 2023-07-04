package com.zyq.bloggy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVo {
    private Integer userId;
    private String username;
    private String nickname;
    private String avatar;
    private String commentId;
    private String content;
    private Date createTime;
    private Boolean isLike;
    private String replyId;
    private Integer likeNum;
    private Integer type;

}
