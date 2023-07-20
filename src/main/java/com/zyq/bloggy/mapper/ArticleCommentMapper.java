package com.zyq.bloggy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.model.pojo.ArticleComment;
import com.zyq.bloggy.model.vo.CommentVo;
import com.zyq.bloggy.model.vo.UserStateVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {
    int addComment(ArticleComment articleComment);

    List<CommentVo> getCommentByArticleId(Long id);

    int delByCommentId(Long id);

    int delByIds(ArrayList<Long> ids);

    List<CommentVo> getCommentByArticleIdLimited(@Param("id") Long id,
                                                 @Param("current") int current,
                                                 @Param("size") int size,
                                                 @Param("userId") long userId);

    int updateLikeNumInt(@Param("id") Long id, @Param("num") int num);

    int addLike(List<ThumbsUp> thumbs);

    int delLike(List<ThumbsUp> thumbs);

    int getIsLike(@Param("commentId") long commentId, @Param("userId") long userId);

    int getLike(@Param("userId") long userId, @Param("commentId") long commentId);

    UserStateVo getUserStateById(long userId);
}
