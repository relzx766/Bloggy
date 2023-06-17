package com.zyq.bloggy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyq.bloggy.pojo.ReplyComment;
import com.zyq.bloggy.vo.CommentVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyCommentMapper extends BaseMapper<ReplyComment> {
    int addComment(ReplyComment replyComment);

    List<CommentVo> getByArtCommentId(Long id);

    List<CommentVo> getByCommentIdLimited(@Param("id") Long id, @Param("current") int current, @Param("size") int size);

}
