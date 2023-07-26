package com.zyq.bloggy.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.zyq.bloggy.model.entity.Result;
import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.model.pojo.Article;
import com.zyq.bloggy.model.vo.ArticleVo;
import com.zyq.bloggy.serivce.ArticleService;
import com.zyq.bloggy.serivce.MailService;
import com.zyq.bloggy.serivce.RedisService;
import com.zyq.bloggy.serivce.SortService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    SortService sortService;
    @Autowired
    RedisService redisService;


    @PostMapping("/publish")
    public Result publish(@RequestBody Article article) {
        Map<String, Object> data = new HashMap<>();
        System.out.println(article);
        data.put("article", articleService.publish(article));
        return Result.ok(data);
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam("id") Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        articleService.delete(id, userId);
        return Result.ok("删除成功");
    }

    @PostMapping("/remove")
    @SaCheckRole("ADMIN")
    public Result remove(@RequestParam("id") Long id
            , @RequestParam("reason") String reason) {
        articleService.delete(id);
        log.info(String.format("管理员{%s}删除了文章，id为{%s}", StpUtil.getLoginIdAsString(), id));
        mailService.sendWorkDeletedAdvice(id, reason);
        return Result.ok("删除成功");
    }

    @PostMapping("/update")
    public Result update(@RequestBody Article article) {
        Long userId = StpUtil.getLoginIdAsLong();
        articleService.update(article, userId);
        return Result.ok();
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable("id") Long id) {
        Map<String, Object> data = new HashMap<>();
        Long userId = StpUtil.getLoginIdAsLong();
        ArticleVo articleVo = articleService.getDetail(id);
        articleVo.setIsLike(articleService.getIsLiked(userId, id));
        articleVo.setIsSort(sortService.getArticleIsSort(id, userId));
        data.put("article", articleVo);
        articleService.addView(id);
        return Result.ok(data);
    }

    @GetMapping("/admin/detail/{id}")
    @SaCheckRole("ADMIN")
    public Result getDetail(@PathVariable("id") Long id) {
        Map<String, Object> data = new HashMap<>();
        ArticleVo articleVo = articleService.getDetailForAdmin(id);
        data.put("article", articleVo);
        return Result.ok(data);
    }

    @PostMapping("/tag")
    @SaIgnore
    public Result getByTag(@RequestParam("tags") String[] tag,
                           @RequestParam("page") Integer page) {
        redisService.incrementTrend(tag);
        return Result.ok(articleService.searchByTag(tag, page));
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
        data.put("count", articleService.getActiveCount());
        return Result.ok(data);
    }

    @GetMapping("/admin/page/{page}")
    @SaCheckRole("ADMIN")
    public Result getPageForAdmin(@PathVariable("page") Integer page) {
        Map<String, Object> data = new HashMap<>();
        data.put("articles", articleService.getPageListForAdmin(page));
        data.put("count", articleService.getRecordCount());
        return Result.ok(data);
    }

    @PostMapping("/active")
    @SaCheckRole("ADMIN")
    public Result active(@RequestParam("id") Long id) {
        articleService.active(id);
        return Result.ok();
    }

    @GetMapping("/user/{id}/{page}")
    @SaIgnore
    public Result getByUser(@PathVariable("id") Long id,
                            @PathVariable("page") Integer page) {
        Map<String, Object> data = new HashMap<>();
        data.put("articles", articleService.getUserList(page, id));
        return Result.ok(data);
    }

    @GetMapping("/user/year/{id}/{year}")
    public Result getUserTree(@PathVariable("id") long id,
                              @PathVariable("year") int year) {
        Map<String, Object> data = new HashMap<>();
        data.put("articles", articleService.getUserRecordsOfYear(id, year));
        return Result.ok(data);
    }

    @GetMapping("/user/year/{id}")
    public Result getUserYear(@PathVariable("id") long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("year", articleService.getUserTimeline(id));
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

    @GetMapping("/count")
    public Result getCount() {
        return new Result().success().put("all", articleService.getRecordCount()).put("active", articleService.getActiveCount());
    }

    @GetMapping("/count/{num}")
    public Result getCountRange(@PathVariable("num") Integer num) {
        return new Result().success().put("records", articleService.getCountRangeByDate(num));
    }
}
