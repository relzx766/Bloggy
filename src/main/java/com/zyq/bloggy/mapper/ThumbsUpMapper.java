package com.zyq.bloggy.mapper;

import com.zyq.bloggy.model.entity.ThumbsUp;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThumbsUpMapper {
    int addToArticle(List<ThumbsUp> thumbs);

    int delFromArticle(List<ThumbsUp> thumbs);

    int getIsLikeArticle(@Param("userId") Integer userId, @Param("articleId") Long articleId);

    int addToArticleComment(List<ThumbsUp> thumbs);

    int delFromArticleComment(List<ThumbsUp> thumbs);

    int getIsLikeArticleComment(@Param("userId") Integer userId, @Param("commentId") Long commentId);

    int addToReplyComment(List<ThumbsUp> thumbs);

    int delFromReplyComment(List<ThumbsUp> thumbs);

    int getIsLikeReplyComment(@Param("userId") Integer userId, @Param("commentId") Long commentId);
}
