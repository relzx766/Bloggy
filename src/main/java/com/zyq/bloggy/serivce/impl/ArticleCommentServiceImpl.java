package com.zyq.bloggy.serivce.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.mapStruct.CommentVoMapper;
import com.zyq.bloggy.mapper.ArticleCommentMapper;
import com.zyq.bloggy.pojo.ArticleComment;
import com.zyq.bloggy.pojo.Status;
import com.zyq.bloggy.pojo.User;
import com.zyq.bloggy.serivce.ArticleCommentService;
import com.zyq.bloggy.serivce.ReplyCommentService;
import com.zyq.bloggy.serivce.UserService;
import com.zyq.bloggy.util.StringUtils;
import com.zyq.bloggy.vo.CommentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@CacheConfig(cacheNames = "articleComment")
public class ArticleCommentServiceImpl implements ArticleCommentService {
    @Autowired
    ArticleCommentMapper articleCommentMapper;
    @Autowired
    ReplyCommentService replyCommentService;
    @Autowired
    CommentVoMapper commentVoMapper;
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public CommentVo post(ArticleComment articleComment) {
        if (StringUtils.isBlank(articleComment.getContent())) {
            throw new BusinessException("内容不能为空");
        }
        articleComment.setId(IdWorker.getId());
        articleComment.setUserId(StpUtil.getLoginIdAsLong());
        articleComment.setLikeNum(0);
        articleComment.setStatus(Status.ACTIVE.getCode());
        articleComment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        articleCommentMapper.addComment(articleComment);
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps("count:article:comment:" + articleComment.getId());
        operations.put("articleId", articleComment.getArticleId().toString());
        operations.increment("count", 1);
        return commentVoMapper.toVo(articleComment);
    }

    @Override
    @Transactional
    public Boolean del(Long id) {
        return articleCommentMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public Boolean del(ArrayList<Long> ids) {
        return articleCommentMapper.delByIds(ids) == ids.size();
    }

    @Override
    public List<ArticleComment> record(Long userId) {
        return articleCommentMapper.selectList(new LambdaQueryWrapper<ArticleComment>()
                .eq(ArticleComment::getUserId, userId)
                .eq(ArticleComment::getStatus, Status.ACTIVE.getCode())
                .orderByDesc(ArticleComment::getCreateTime));
    }

    @Override
    @Deprecated
    public List<CommentVo> ArticleComments(Long id) {
        List<ArticleComment> comments = articleCommentMapper
                .selectList(new LambdaQueryWrapper<ArticleComment>()
                        .eq(ArticleComment::getArticleId, id).eq(ArticleComment::getStatus, Status.ACTIVE.getCode())
                ).stream().distinct().collect(Collectors.toList());
        List<Long> userIds = comments.stream().map(c -> c.getUserId()).collect(Collectors.toList());
        Map<Long, User> userMap = userService.getUser(userIds).stream().collect(Collectors.toMap(User::getId, user -> user));
        return comments.stream().map(c -> commentVoMapper.toVo(userMap.get(c.getUserId()), c)).collect(Collectors.toList());
    }

    @Override
    @Cacheable(key = "#id")
    public List<CommentVo> getComments(Long id) {
        return articleCommentMapper.getCommentByArticleId(id);
    }

    @Override
    public Page<CommentVo> getComments(Long id, int page) {
        page = page > 0 ? page : 0;
        int size = 15;
        int current = (page - 1) * size;
        Page<CommentVo> voPage = new Page<>();
        List<CommentVo> record = articleCommentMapper.getCommentByArticleIdLimited(id, current, size);
        voPage.setRecords(record);
        voPage.setCurrent(page);
        voPage.setSize(record.size());
        voPage.setTotal(articleCommentMapper.selectCount(new LambdaQueryWrapper<ArticleComment>().eq(ArticleComment::getArticleId, id)));
        return voPage;
    }
}
