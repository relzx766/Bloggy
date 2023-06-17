package com.zyq.bloggy.serivce.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.mapStruct.CommentVoMapper;
import com.zyq.bloggy.mapper.ReplyCommentMapper;
import com.zyq.bloggy.pojo.ReplyComment;
import com.zyq.bloggy.pojo.Status;
import com.zyq.bloggy.serivce.ReplyCommentService;
import com.zyq.bloggy.serivce.UserService;
import com.zyq.bloggy.util.StringUtils;
import com.zyq.bloggy.vo.CommentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@CacheConfig(cacheNames = "replyComment")
public class ReplyCommentServiceImpl implements ReplyCommentService {
    @Autowired
    ReplyCommentMapper replyCommentMapper;
    @Autowired
    CommentVoMapper commentVoMapper;
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;

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
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps("count:article:comment:" + replyComment.getCommentId());
        operations.increment("count", 1);
        return commentVoMapper.toVo(replyComment);
    }

    @Override
    public Boolean del(Long id) {
        return replyCommentMapper.deleteById(id) > 0;
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
    public List<CommentVo> getComments(Long id) {
        return replyCommentMapper.getByArtCommentId(id);
    }

    @Override
    public Page<CommentVo> getComments(Long id, int page) {
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
}
