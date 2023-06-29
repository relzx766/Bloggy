package com.zyq.bloggy.controller;

import com.zyq.bloggy.model.entity.Result;
import com.zyq.bloggy.util.FileUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {
    @PostMapping("/upload/image")
    public Result uploadImage(@RequestParam("image") MultipartFile multipartFile) {
        String path = FileUtil.save(multipartFile);
        Map<String, Object> data = new HashMap<>();
        data.put("path", path);
        return Result.ok(data);
    }
}
