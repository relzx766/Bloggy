package com.zyq.bloggy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.model.pojo.ReplyComment;
import com.zyq.bloggy.model.vo.CommentVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyCommentMapper extends BaseMapper<ReplyComment> {
    int addComment(ReplyComment replyComment);

    List<CommentVo> getByArtCommentId(@Param("id") Long id, @Param("userId") long userId);

    List<CommentVo> getByCommentIdLimited(@Param("id") Long id,
                                          @Param("current") int current,
                                          @Param("size") int size);

    int addLike(List<ThumbsUp> thumbs);

    int delLike(List<ThumbsUp> thumbs);

    int updateLikeNum(@Param("id") long id, @Param("num") int num);

    int getIsLike(@Param("commentId") long commentId, @Param("userId") long userId);
}
