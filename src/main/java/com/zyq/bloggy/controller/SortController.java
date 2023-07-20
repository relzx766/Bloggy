package com.zyq.bloggy.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.zyq.bloggy.model.entity.Result;
import com.zyq.bloggy.model.pojo.Sort;
import com.zyq.bloggy.serivce.SortService;
import com.zyq.bloggy.util.FileUtil;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sort")
public class SortController {
    @Autowired
    SortService sortService;

    @PostMapping("/create")
    public Result create(@Nullable @RequestParam("cover") MultipartFile multipartFile, @Nullable @RequestParam("title") String title) {
        Sort sort = new Sort();
        sort.setTitle(title);
        if (!FileUtil.isBlank(multipartFile)) {
            sort.setCover(FileUtil.save(multipartFile));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("sort", sortService.addSort(sort));
        return Result.ok(data);
    }

    @PostMapping("/del")
    public Result delete(@RequestParam("id") Integer id) {
        return sortService.delSort(id) ? Result.ok("归档删除成功") : Result.err("归档删除失败");
    }

    @PostMapping("/update")
    public Result update(@RequestParam("id") Integer id,
                         @RequestParam("title") String title,
                         @RequestParam("cover") MultipartFile cover) {
        Sort sort = new Sort();
        sort.setId(id);
        sort.setTitle(title);
        sort.setOwner(StpUtil.getLoginIdAsLong());
        if (!FileUtil.isBlank(cover)) {
            sort.setCover(FileUtil.save(cover));
        }
        return sortService.updateSort(sort) ? Result.ok("归档更新成功") : Result.err("归档更新失败");
    }

    @GetMapping("/detail/{id}")
    public Result getDetail(@PathVariable("id") Integer id) {
        Map<String, Object> data = sortService.getDetail(id);
        return Result.ok(data);
    }

    @PostMapping("/append")
    public Result append(@RequestParam("sortId") Integer sortId,
                         @RequestParam("articleId") Long articleId) {
        sortService.addArticle(sortId, articleId, StpUtil.getLoginIdAsLong());
        return Result.ok("归档成功");
    }

    @PostMapping("/remove")
    public Result remove(@RequestParam("sortId") Integer sortId,
                         @RequestParam("articleId") Long articleId) {
        sortService.delArticle(sortId, articleId, StpUtil.getLoginIdAsLong());

        return Result.ok();
    }

    @PostMapping("/cancel")
    public Result cancel(@RequestParam("articleId") Long articleId) {
        Long userId = StpUtil.getLoginIdAsLong();
        sortService.cancelSort(articleId, userId);
        return Result.ok();
    }

    @GetMapping("/user/{id}")
    public Result getListOfUser(@PathVariable("id") Long userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("sorts", sortService.getUserSort(userId));
        return Result.ok(data);
    }

}
