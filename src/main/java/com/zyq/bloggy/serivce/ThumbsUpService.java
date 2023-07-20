package com.zyq.bloggy.serivce;

import com.zyq.bloggy.model.entity.ThumbsUp;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Deprecated
public interface ThumbsUpService {
    //缓存到redis
    void likeArticle(ThumbsUp thumbs);

    void cancelLikeArticle(ThumbsUp thumbs);

    void likeArticleComment(ThumbsUp thumbs);

    void cancelLikeArticleComment(ThumbsUp thumbs);

    void likeReplyComment(ThumbsUp thumbs);

    void cancelLikeReplyComment(ThumbsUp thumbs);

    Map<String, Object> getStruct(Integer status, Timestamp time);
    //以下都为持久化操作,定时任务从redis存储到mysql

    Boolean delArticleLike(List<ThumbsUp> thumbs);

    Boolean isLikedArticle(Integer userId, Long articleId);

    Boolean delArticleCommentLike(List<ThumbsUp> thumbs);

    Boolean isLikedArticleComment(Integer userId, Long articleId);

    Boolean delReplyCommentLike(List<ThumbsUp> thumbs);

    Boolean isLikedReplyComment(Integer userId, Long articleId);

    void saveArticleLike();

    void saveArticleCommentLike();

    void saveReplyCommentLike();
}
