package com.zyq.bloggy.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.zyq.bloggy.model.entity.Result;
import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.model.pojo.ArticleComment;
import com.zyq.bloggy.model.pojo.ReplyComment;
import com.zyq.bloggy.serivce.ArticleCommentService;
import com.zyq.bloggy.serivce.ReplyCommentService;
import com.zyq.bloggy.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    ArticleCommentService articleCommentService;
    @Autowired
    ReplyCommentService replyCommentService;

    @PostMapping("/article/post")
    public Result postComment(@RequestBody ArticleComment articleComment) {
        if (!StringUtils.isBlank(articleComment.getContent())) {
            Map<String, Object> data = new HashMap<>();
            data.put("comment", articleCommentService.post(articleComment));
            return Result.ok(data);
        }
        return Result.err("内容不能为空");
    }

    @PostMapping("/delete/article")
    public Result delete(@RequestParam("id") Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        return articleCommentService.del(id, userId) ? Result.ok() : Result.err();
    }

    @PostMapping("/delete/article/admin")
    @SaCheckRole("ADMIN")
    public Result deletes(@RequestParam("ids") ArrayList<Long> ids) {
        return articleCommentService.del(ids) ? Result.ok() : Result.err();
    }

    @GetMapping("/record/{id}")
    public Result record(@PathVariable("id") Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("comment", articleCommentService.record(id));
        data.put("reply", articleCommentService.record(id));
        return Result.ok(data);
    }

    @GetMapping("/article/{id}")
    public Result getComment(@PathVariable("id") Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("comment", articleCommentService.getComments(id));
        return Result.ok(data);
    }

    @GetMapping("/article/{articleId}/{page}")
    public Result getCommentPage(@PathVariable("articleId") Long articleId,
                                 @PathVariable("page") Integer page) {
        Map<String, Object> data = new HashMap<>();
        Long userId = StpUtil.getLoginIdAsLong();
        data.put("comment", articleCommentService.getComments(articleId, page, userId));
        return Result.ok(data);
    }

    @GetMapping("/reply/{commentId}")
    public Result getReply(@PathVariable("commentId") Long id) {
        Map<String, Object> data = new HashMap<>();
        Long userId = StpUtil.getLoginIdAsLong();
        data.put("reply", replyCommentService.getReplies(id, userId));
        return Result.ok(data);
    }

    @PostMapping("/reply/post")
    public Result ReplyPost(@RequestBody ReplyComment replyComment) {
        Map<String, Object> data = new HashMap<>();
        data.put("reply", replyCommentService.post(replyComment));
        return Result.ok(data);
    }

    @PostMapping("/delete/reply")
    public Result ReplyDel(@RequestParam("id") Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        return replyCommentService.del(id, userId) ? Result.ok() : Result.err();
    }

    @PostMapping("/delete/reply/admin")
    @SaCheckRole("ADMIN")
    public Result ReplyDelMulti(@RequestParam("ids") ArrayList<Long> ids) {
        return replyCommentService.del(ids) ? Result.ok() : Result.err();
    }

    @GetMapping("/reply/{commentId}/{page}")
    public Result getReplyPage(@PathVariable("commentId") Long id,
                               @PathVariable("page") Integer page) {
        Map<String, Object> data = new HashMap<>();
        data.put("reply", replyCommentService.getReplies(id, page));
        return Result.ok(data);
    }

    @PostMapping("/like")
    public Result likeComment(@RequestParam("commentId") Long commentId) {
        ThumbsUp thumbs = new ThumbsUp();
        thumbs.setFromId(StpUtil.getLoginIdAsLong());
        thumbs.setToId(commentId);
        articleCommentService.like(thumbs);
        return Result.ok();
    }

    @PostMapping("/like/reply")
    public Result likeReply(@RequestParam("replyId") Long replyId) {
        ThumbsUp thumbs = new ThumbsUp();
        thumbs.setFromId(StpUtil.getLoginIdAsLong());
        thumbs.setToId(replyId);
        replyCommentService.like(thumbs);
        return Result.ok();
    }

    @PostMapping("/like/cancel")
    public Result cancelLikeComment(@RequestParam("commentId") Long commentId) {
        ThumbsUp thumbs = new ThumbsUp();
        thumbs.setFromId(StpUtil.getLoginIdAsLong());
        thumbs.setToId(commentId);
        articleCommentService.cancelLike(thumbs);
        return Result.ok();
    }

    @PostMapping("/like/reply/cancel")
    public Result cancelLikeReply(@RequestParam("replyId") Long replyId) {
        ThumbsUp thumbs = new ThumbsUp();
        thumbs.setFromId(StpUtil.getLoginIdAsLong());
        thumbs.setToId(replyId);
        replyCommentService.cancelLike(thumbs);
        return Result.ok();
    }

    @GetMapping("/like/get/{id}")
    public Result getIsLikeComment(@PathVariable("id") Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("isLike", articleCommentService.getIsLiked(StpUtil.getLoginIdAsLong(), id));
        return Result.ok(data);
    }

    @GetMapping("/like/reply/get/{id}")
    public Result getIsLikeReply(@PathVariable("id") Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("isLike", replyCommentService.getIsLiked(StpUtil.getLoginIdAsLong(), id));
        return Result.ok(data);
    }
}
