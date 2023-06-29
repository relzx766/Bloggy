package com.zyq.bloggy.serivce.impl;

import com.zyq.bloggy.exception.ServiceException;
import com.zyq.bloggy.mapper.ThumbsUpMapper;
import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.serivce.ThumbsUpService;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.shaded.freemarker.ext.util.IdentityHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class ThumbsUpServiceImpl implements ThumbsUpService {

    @Autowired
    ThumbsUpMapper thumbsUpMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public synchronized void likeArticle(ThumbsUp thumbs) {
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps("ArticleLike:");
        String key = thumbs.getFromId() + ":" + thumbs.getToId();
        try {
            operations.put(key, getStruct(1, thumbs.getCreateTime()));
            redisTemplate.opsForValue().increment("ArticleLikeNum:" + thumbs.getToId() + ":", 1);
        } catch (Exception e) {
            throw new ServiceException("缓存故障");
        }

    }

    @Override
    public synchronized void cancelLikeArticle(ThumbsUp thumbs) {
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps("ArticleLike:");
        String key = thumbs.getFromId() + ":" + thumbs.getToId();
        try {
            operations.put(key, getStruct(0, thumbs.getCreateTime()));
            redisTemplate.opsForValue().increment("ArticleLikeNum:" + thumbs.getToId() + ":", -1);
        } catch (Exception e) {
            throw new ServiceException("缓存故障");
        }
    }

    @Override
    public synchronized void likeArticleComment(ThumbsUp thumbs) {
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps("CommentLike:");
        String key = thumbs.getFromId() + ":" + thumbs.getToId();
        try {
            operations.put(key, getStruct(1, thumbs.getCreateTime()));
            redisTemplate.opsForValue().increment("CommentLikeNum:" + thumbs.getToId() + ":", 1);
        } catch (Exception e) {
            throw new ServiceException("缓存故障");
        }
    }

    @Override
    public synchronized void cancelLikeArticleComment(ThumbsUp thumbs) {
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps("CommentLike:");
        String key = thumbs.getFromId() + ":" + thumbs.getToId();
        try {
            operations.put(key, getStruct(0, thumbs.getCreateTime()));
            redisTemplate.opsForValue().increment("CommentLikeNum:" + thumbs.getToId() + ":", -1);
        } catch (Exception e) {
            throw new ServiceException("缓存故障");
        }
    }

    @Override
    public synchronized void likeReplyComment(ThumbsUp thumbs) {
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps("ReplyLike:");
        String key = thumbs.getFromId() + ":" + thumbs.getToId();
        try {
            operations.put(key, getStruct(1, thumbs.getCreateTime()));
            redisTemplate.opsForValue().increment("ReplyLikeNum:" + thumbs.getToId() + ":", 1);
        } catch (Exception e) {
            throw new ServiceException("缓存故障");
        }
    }

    @Override
    public synchronized void cancelLikeReplyComment(ThumbsUp thumbs) {
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps("ReplyLike:");
        String key = thumbs.getFromId() + ":" + thumbs.getToId();
        try {
            operations.put(key, getStruct(0, thumbs.getCreateTime()));
            redisTemplate.opsForValue().increment("ReplyLikeNum:" + thumbs.getToId() + ":", -1);
        } catch (Exception e) {
            throw new ServiceException("缓存故障");
        }
    }

    @Override
    public Map<String, Object> getStruct(Integer status, Timestamp time) {
        Map<String, Object> map = new IdentityHashMap();
        map.put("status", status);
        map.put("createTime", time);
        return map;
    }


    @Override
    public Boolean delArticleLike(List<ThumbsUp> thumbs) {
        return thumbsUpMapper.delFromArticle(thumbs) == thumbs.size();
    }

    @Override
    @Cacheable(cacheNames = "ArticleLiked", key = "#userId+':'+#articleId")
    public Boolean isLikedArticle(Integer userId, Long articleId) {
        return thumbsUpMapper.getIsLikeArticle(userId, articleId) > 0;
    }


    @Override
    public Boolean delArticleCommentLike(List<ThumbsUp> thumbs) {
        return thumbsUpMapper.delFromArticleComment(thumbs) == thumbs.size();
    }

    @Override
    @Cacheable(cacheNames = "CommentLiked", key = "#userId+':'+#articleId")
    public Boolean isLikedArticleComment(Integer userId, Long articleId) {
        return thumbsUpMapper.getIsLikeArticleComment(userId, articleId) > 0;
    }


    @Override
    public Boolean delReplyCommentLike(List<ThumbsUp> thumbs) {
        return thumbsUpMapper.delFromReplyComment(thumbs) == thumbs.size();
    }

    @Override
    @Cacheable(cacheNames = "ReplyLiked", key = "#userId+':'+#articleId")
    public Boolean isLikedReplyComment(Integer userId, Long articleId) {
        return thumbsUpMapper.getIsLikeReplyComment(userId, articleId) > 0;
    }

    @Override
    public void saveArticleLike() {

    }

    @Override
    public void saveArticleCommentLike() {

    }

    @Override
    public void saveReplyCommentLike() {

    }
}
