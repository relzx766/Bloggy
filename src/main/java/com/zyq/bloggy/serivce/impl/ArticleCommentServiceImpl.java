package com.zyq.bloggy.serivce.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.annotation.TaskInfo;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.mapStruct.CommentVoMapper;
import com.zyq.bloggy.mapper.ArticleCommentMapper;
import com.zyq.bloggy.model.entity.Status;
import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.model.pojo.ArticleComment;
import com.zyq.bloggy.model.pojo.User;
import com.zyq.bloggy.model.vo.CommentVo;
import com.zyq.bloggy.serivce.ArticleCommentService;
import com.zyq.bloggy.serivce.RedisService;
import com.zyq.bloggy.serivce.ReplyCommentService;
import com.zyq.bloggy.serivce.UserService;
import com.zyq.bloggy.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@CacheConfig(cacheNames = "articleComment")
public class ArticleCommentServiceImpl implements ArticleCommentService {
    public static final String KEY_COMMENT_LIKE_COUNT = "count:like:comment";
    public static final String KEY_COMMENT_LIKE = "like:comment";
    private static final String KEY_ARTICLE_COMMENT_COUNT = "count:comment:article";
    private static final String KEY_COMMENT_IS_LIKE = "isLike:comment";
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
    @Autowired
    RedisService redisService;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        //以评论id作为键，方便二级评论统计
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps(KEY_ARTICLE_COMMENT_COUNT + ":" + articleComment.getId());
        operations.put("articleId", articleComment.getArticleId().toString());
        operations.increment("count", 1);
        return commentVoMapper.toVo(articleComment);
    }

    @Override
    @Transactional
    public Boolean del(Long id, Long userId) {
        return articleCommentMapper.delete(new LambdaQueryWrapper<ArticleComment>()
                .eq(ArticleComment::getId, id).eq(ArticleComment::getUserId, userId)) > 0;
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
    public Page<CommentVo> getComments(Long id, int page, Long userId) {
        page = page > 0 ? page : 0;
        int size = 15;
        int current = (page - 1) * size;
        Page<CommentVo> voPage = new Page<>();
        List<CommentVo> record = articleCommentMapper.getCommentByArticleIdLimited(id, current, size, userId);
        voPage.setRecords(record);
        voPage.setCurrent(page);
        voPage.setSize(record.size());
        voPage.setTotal(articleCommentMapper.selectCount(new LambdaQueryWrapper<ArticleComment>().eq(ArticleComment::getArticleId, id)));
        return voPage;
    }

    @Override
    public void like(ThumbsUp thumbs) {
        redisService.like(KEY_COMMENT_LIKE, thumbs);
    }

    @Override
    public void cancelLike(ThumbsUp thumbs) {
        redisService.cancelLike(KEY_COMMENT_LIKE, thumbs);
    }

    @Override
    public void updateLikeNum(long id, int num) {
        articleCommentMapper.updateLikeNumInt(id, num);
    }

    @Override
    @Async
    @Scheduled(cron = "0 */2 * * * *")
    @TaskInfo(group = "articleComment", value = "save like", description = "保存一级评论点赞消息")
    public void saveLikeToDB() {
        Map<Long, List<ThumbsUp>> like = redisService.getLike(KEY_COMMENT_LIKE);
        Map<Long, List<ThumbsUp>> cancel = redisService.getCancelLike(KEY_COMMENT_LIKE);
        like.forEach((commentId, thumbs) -> {
            //获取的为commentId:thumbs形式，即使存在commentId，thumbs还是可能为空的
            if (thumbs.size() > 0) {
                int countOfNewLike = articleCommentMapper.addLike(thumbs);
                updateLikeNum(commentId, countOfNewLike);
            }
        });
        cancel.forEach((commentId, thumbs) -> {
            if (thumbs.size() > 0) {
                int countOfCancelLike = articleCommentMapper.delLike(thumbs);
                updateLikeNum(commentId, -countOfCancelLike);
            }
        });

    }

    @Override
    @Cacheable(cacheNames = "isLike:comment#60", key = "#commentId+':'+#userId")
    public boolean getIsLiked(Long userId, Long commentId) {
        return articleCommentMapper.getIsLike(commentId, userId) > 0;
    }
}
