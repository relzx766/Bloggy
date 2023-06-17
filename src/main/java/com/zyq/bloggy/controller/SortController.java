package com.zyq.bloggy.controller;

import com.zyq.bloggy.pojo.Result;
import com.zyq.bloggy.pojo.Sort;
import com.zyq.bloggy.serivce.SortService;
import com.zyq.bloggy.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/sort")
public class SortController {
    @Autowired
    SortService sortService;

    @PostMapping("/create")
    public Result create(@RequestParam("cover") MultipartFile multipartFile, @RequestParam("title") String title) {
        Sort sort = new Sort();
        sort.setTitle(title);
        if (!multipartFile.isEmpty()) {
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
        sort.setCover(FileUtil.save(cover));
        return sortService.updateSort(sort) ? Result.ok("归档更新成功") : Result.err("归档更新失败");
    }

    @GetMapping("/detail/{id}")
    public Result getDetail(@PathVariable("id") Integer id) {
        Map<String, Object> data = new HashMap<>();
        data.put("sort", sortService.getDetail(id));
        return Result.ok(data);
    }

    @PostMapping("/append")
    public Result append(@RequestParam("sortId") Integer sortId,
                         @RequestParam("articleId") Long articleId) {
        Sort sort = sortService.addArticle(sortId, articleId);
        if (Objects.nonNull(sort)) {
            Map<String, Object> data = new HashMap<>();
            data.put("sort", sort);
            return Result.ok(data, "归档成功");
        }
        return Result.err("归档失败");
    }

    @PostMapping("/remove")
    public Result remove(@RequestParam("sortId") Integer sortId,
                         @RequestParam("articleId") Long articleId) {
        Sort sort = sortService.delArticle(sortId, articleId);
        if (Objects.nonNull(sort)) {
            Map<String, Object> data = new HashMap<>();
            data.put("sort", sort);
            return Result.ok(data, "移除成功");
        }
        return Result.err("移除失败");
    }

}
