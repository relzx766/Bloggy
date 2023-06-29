package com.zyq.bloggy.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_reply_comment")
public class ReplyComment {
    @TableId(value = "reply_comment_id", type = IdType.ASSIGN_ID)
    private Long id;
    private Long commentId;
    private Long userId;
    private String content;
    private Integer likeNum;
    //评论类型，1为二级评论，2为三级评论
    private Integer type;
    @TableLogic
    private Integer status;
    private Timestamp createTime;
}
