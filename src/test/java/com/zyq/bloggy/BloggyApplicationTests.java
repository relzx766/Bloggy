package com.zyq.bloggy;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zyq.bloggy.mapStruct.ArticleVoMapper;
import com.zyq.bloggy.mapper.ArticleCommentMapper;
import com.zyq.bloggy.mapper.ArticleMapper;
import com.zyq.bloggy.mapper.ReplyCommentMapper;
import com.zyq.bloggy.model.pojo.Article;
import com.zyq.bloggy.serivce.*;
import com.zyq.bloggy.util.TimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.FileNotFoundException;
import java.sql.Timestamp;

@SpringBootTest
class BloggyApplicationTests {
    @Autowired
    ThumbsUpService thumbsUpService;
    @Autowired
    MailService mailService;
    @Autowired
    ArticleService articleService;
    @Autowired
    UserService userService;
    @Autowired
    ArticleCommentService articleCommentService;
    @Autowired
    ReplyCommentService replyCommentService;
    @Autowired
    ArticleCommentMapper articleCommentMapper;
    @Autowired
    ReplyCommentMapper replyCommentMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ArticleVoMapper articleVoMapper;
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    RedisService redisService;

    @Test
    void contextLoads() throws FileNotFoundException {
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps("count:comment:article");
        System.out.println(redisTemplate.keys("count:comment:article*"));

    }

}
