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
@TableName("tb_article_comment")
public class ArticleComment {
    @TableId(value = "article_comment_id", type = IdType.ASSIGN_ID)
    private Long id;
    private Long articleId;
    private Long userId;
    private String content;
    private Integer likeNum;
    @TableLogic
    private Integer status;
    private Timestamp createTime;
}
