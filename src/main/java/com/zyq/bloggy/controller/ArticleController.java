package com.zyq.bloggy.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.zyq.bloggy.pojo.Article;
import com.zyq.bloggy.pojo.Result;
import com.zyq.bloggy.pojo.ThumbsUp;
import com.zyq.bloggy.serivce.ArticleService;
import com.zyq.bloggy.serivce.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/article")
@Slf4j
public class ArticleController {
    @Autowired
    ArticleService articleService;
    @Autowired
    MailService mailService;


    @PostMapping("/publish")
    public Result publish(@RequestBody Article article) {
        Map<String, Object> data = new HashMap<>();
        data.put("article", articleService.publish(article));
        return Result.ok(data);
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam("ids") ArrayList<Long> ids) {
        articleService.delete(ids);
        return Result.ok("删除成功");
    }

    @PostMapping("/remove")
    @SaCheckRole("ADMIN")
    public Result remove(@RequestParam("id") Long id
            , @RequestParam("reason") String reason) {
        if (articleService.delete(id)) {
            log.info(String.format("管理员{%s}删除了文章，id为{%s}", StpUtil.getLoginIdAsString(), id));
            mailService.sendWorkDeletedAdvice(id, reason);
            return Result.ok("删除成功");
        }
        return Result.err("删除失败");
    }

    @PostMapping("/update")
    public Result update(@RequestBody Article article) {
        if (StpUtil.getLoginIdAsLong() == articleService.getById(article.getId()).getAuthor()) {
            if (!article.getTitle().isEmpty()) {
                if (articleService.update(article)) {
                    return Result.ok("更新成功");
                }
            }
        }
        return Result.err("更新失败");
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable("id") Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("article", articleService.getDetail(id));
        articleService.addView(id);
        return Result.ok(data);
    }

    @GetMapping("/tag/{tag}")
    @SaIgnore
    public Result getByTag(@PathVariable("tag") String tag) {
        Map<String, Object> data = new HashMap<>();
        data.put("articles", articleService.searchByTag(tag));
        return Result.ok(data);
    }

    @GetMapping("/search/{keyword}/{page}")
    @SaIgnore
    public Result search(@PathVariable("keyword") String keyword,
                         @PathVariable("page") Integer page) {
        Map<String, Object> data = new HashMap<>();
        data.put("articles", articleService.fuzzySearch(keyword, page));
        data.put("count", articleService.getSearchCount(keyword));
        return Result.ok(data);
    }

    @GetMapping("/page/{page}")
    public Result getPage(@PathVariable("page") Integer page) {
        Map<String, Object> data = new HashMap<>();
        data.put("articles", articleService.getPageList(page));
        data.put("count", articleService.getRecordCount());
        return Result.ok(data);
    }

    @GetMapping("/user/{id}/{page}")
    @SaIgnore
    public Result getByUser(@PathVariable("id") Long id,
                            @PathVariable("page") Integer page) {
        Map<String, Object> data = new HashMap<>();
        data.put("articles", articleService.getUserList(page, id));
        return Result.ok(data);
    }


    @GetMapping("/like/{articleId}")
    public Result like(@PathVariable("articleId") Long articleId) {
        Long userId = StpUtil.getLoginIdAsLong();
        ThumbsUp thumbs = new ThumbsUp();
        thumbs.setFromId(userId);
        thumbs.setToId(articleId);
        articleService.like(thumbs);
        return Result.ok();
    }

    @GetMapping("/like/cancel/{articleId}")
    public Result cancelLike(@PathVariable("articleId") Long articleId) {
        Long userId = StpUtil.getLoginIdAsLong();
        ThumbsUp thumbs = new ThumbsUp();
        thumbs.setFromId(userId);
        thumbs.setToId(articleId);
        articleService.cancelLike(thumbs);
        return Result.ok();
    }

    @GetMapping("/like/get/{articleId}")
    public Result getIsLike(@PathVariable("articleId") Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        Map<String, Object> data = new HashMap<>();
        data.put("isLike", articleService.getIsLiked(userId, id));
        return Result.ok(data);
    }

}
