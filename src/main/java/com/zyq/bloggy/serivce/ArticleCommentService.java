package com.zyq.bloggy.serivce;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.pojo.ArticleComment;
import com.zyq.bloggy.vo.CommentVo;

import java.util.ArrayList;
import java.util.List;

public interface ArticleCommentService {
    CommentVo post(ArticleComment articleComment);

    Boolean del(Long id);

    Boolean del(ArrayList<Long> ids);

    List<ArticleComment> record(Long userId);

    @Deprecated
    List<CommentVo> ArticleComments(Long id);

    List<CommentVo> getComments(Long id);

    //获取文章下一级评论分页
    Page<CommentVo> getComments(Long id, int page);
}
