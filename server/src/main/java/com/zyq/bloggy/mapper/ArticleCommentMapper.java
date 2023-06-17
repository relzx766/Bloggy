package com.zyq.bloggy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyq.bloggy.pojo.ArticleComment;
import com.zyq.bloggy.vo.CommentVo;
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

    List<CommentVo> getCommentByArticleIdLimited(@Param("id") Long id, @Param("current") int current, @Param("size") int size);

}
