package com.zyq.bloggy.serivce.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.annotation.TaskInfo;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.mapStruct.CommentVoMapper;
import com.zyq.bloggy.mapper.ReplyCommentMapper;
import com.zyq.bloggy.model.entity.Status;
import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.model.pojo.ReplyComment;
import com.zyq.bloggy.model.vo.CommentVo;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@CacheConfig(cacheNames = "replyComment")
public class ReplyCommentServiceImpl implements ReplyCommentService {
    private static final String KEY_REPLY_LIKE_COUNT = "count:like:reply";
    private static final String KEY_REPLY_LIKE = "like:reply";
    private static final String KEY_ARTICLE_COMMENT_COUNT = "count:comment:article";
    private static final String KEY_REPLY_IS_LIKE = "isLike:reply";
    @Autowired
    ReplyCommentMapper replyCommentMapper;
    @Autowired
    CommentVoMapper commentVoMapper;
    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;
    @Autowired
    RedisTemplate redisTemplate;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public CommentVo post(ReplyComment replyComment) {
        if (StringUtils.isBlank(replyComment.getContent())) {
            throw new BusinessException("内容不能为空");
        }
        if (Objects.isNull(replyComment.getCommentId())) {
            throw new BusinessException("回复对象不能为空");
        }
        replyComment.setId(IdWorker.getId());
        replyComment.setLikeNum(0);
        replyComment.setUserId(StpUtil.getLoginIdAsLong());
        replyComment.setStatus(Status.ACTIVE.getCode());
        replyComment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        replyCommentMapper.addComment(replyComment);
        //以一级评论的id作为键
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps(KEY_ARTICLE_COMMENT_COUNT + ":" + replyComment.getCommentId());
        operations.increment("count", 1);
        return commentVoMapper.toVo(replyComment);
    }

    @Override
    public Boolean del(Long id, Long userId) {
        return replyCommentMapper.delete(new LambdaQueryWrapper<ReplyComment>()
                .eq(ReplyComment::getId, id).eq(ReplyComment::getUserId, userId)) > 0;
    }

    @Override

    public Boolean del(ArrayList<Long> ids) {
        return replyCommentMapper.deleteBatchIds(ids) == ids.size();
    }

    @Override
    public List<ReplyComment> record(Long userId) {
        return replyCommentMapper.selectList(new LambdaQueryWrapper<ReplyComment>()
                .eq(ReplyComment::getUserId, userId)
                .eq(ReplyComment::getStatus, Status.ACTIVE.getCode())
                .orderByDesc(ReplyComment::getCreateTime)
        );
    }

    @Override
    public List<CommentVo> getReplies(Long id, Long userId) {
        return replyCommentMapper.getByArtCommentId(id, userId);
    }

    @Override
    public Page<CommentVo> getReplies(Long id, int page) {
        page = page > 0 ? page : 0;
        int size = 10;
        int current = (page - 1) * size;
        Page<CommentVo> voPage = new Page<>();
        List<CommentVo> record = replyCommentMapper.getByCommentIdLimited(id, current, size);
        voPage.setRecords(record);
        voPage.setSize(record.size());
        voPage.setCurrent(current);
        voPage.setTotal(replyCommentMapper.selectCount(new LambdaQueryWrapper<ReplyComment>()
                .eq(ReplyComment::getCommentId, id)));
        return voPage;
    }

    @Override
    public void like(ThumbsUp thumbs) {
        redisService.like(KEY_REPLY_LIKE, thumbs);
    }

    @Override
    public void cancelLike(ThumbsUp thumbs) {
        redisService.cancelLike(KEY_REPLY_LIKE, thumbs);
    }

    @Override
    @Async
    @Scheduled(cron = "0 */2 * * * *")
    @TaskInfo(group = "replyComment", value = "update like", description = "更新二级评论点赞信息")
    public void saveLikeToDB() {
        Map<Long, List<ThumbsUp>> like = redisService.getLike(KEY_REPLY_LIKE);
        Map<Long, List<ThumbsUp>> cancel = redisService.getCancelLike(KEY_REPLY_LIKE);
        like.forEach((replyId, thumbs) -> {
            if (thumbs.size() > 0) {
                int countOfNewLike = replyCommentMapper.addLike(thumbs);
                updateLikeNum(replyId, countOfNewLike);
            }
        });
        cancel.forEach((replyId, thumbs) -> {
            if (thumbs.size() > 0) {
                int countOfCancelLike = replyCommentMapper.delLike(thumbs);
                updateLikeNum(replyId, -countOfCancelLike);
            }
        });
    }

    @Override
    public void updateLikeNum(long id, int num) {
        BoundHashOperations<String, String, Integer> operations = redisTemplate.boundHashOps(KEY_REPLY_LIKE_COUNT);
        operations.keys().stream().forEach(key -> {
            int count = operations.get(key);
            replyCommentMapper.updateLikeNum(Long.parseLong(key), count);
            operations.delete(key);
        });
    }

    @Override
    @Cacheable(cacheNames = "isLike:reply#60", key = "#replyId+':'+#userId")
    public boolean getIsLiked(Long userId, Long replyId) {
        return replyCommentMapper.getIsLike(replyId, userId) > 0;
    }
}
