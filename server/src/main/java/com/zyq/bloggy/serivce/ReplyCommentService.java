package com.zyq.bloggy.serivce;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.pojo.ReplyComment;
import com.zyq.bloggy.vo.CommentVo;

import java.util.ArrayList;
import java.util.List;

public interface ReplyCommentService {
    CommentVo post(ReplyComment replyComment);

    Boolean del(Long id);

    Boolean del(ArrayList<Long> ids);

    List<ReplyComment> record(Long userId);

    List<CommentVo> getComments(Long id);

    Page<CommentVo> getComments(Long id, int page);

}
