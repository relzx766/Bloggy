package com.zyq.bloggy.serivce.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.annotation.TrendCounter;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.mapStruct.ArticleVoMapper;
import com.zyq.bloggy.mapper.ArticleMapper;
import com.zyq.bloggy.pojo.Article;
import com.zyq.bloggy.pojo.Status;
import com.zyq.bloggy.pojo.ThumbsUp;
import com.zyq.bloggy.serivce.ArticleService;
import com.zyq.bloggy.serivce.TagService;
import com.zyq.bloggy.serivce.ThumbsUpService;
import com.zyq.bloggy.serivce.UserService;
import com.zyq.bloggy.util.StringUtils;
import com.zyq.bloggy.vo.ArticleVo;
import com.zyq.bloggy.vo.UserVo;
import io.github.ms100.cacheasmulti.cache.annotation.CacheAsMulti;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@CacheConfig(cacheNames = "article")
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    UserService userService;
    @Autowired
    TagService tagService;
    @Autowired
    ArticleVoMapper articleVoMapper;
    @Autowired
    ThumbsUpService thumbsUpService;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public Article publish(Article article) {
        if (StringUtils.isAnyBlank(article.getTitle(), article.getContent())) {
            throw new BusinessException("标题和内容不能为空");
        }
        article.setId(IdWorker.getId());
        article.setAuthor(StpUtil.getLoginIdAsLong());
        article.setLikeNum(0);
        article.setViews(0);
        article.setComments(0);
        article.setStatus(Status.ACTIVE.getCode());
        article.setCreateTime(new Timestamp(System.currentTimeMillis()));
        article.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        String content = article.getContent();
        String description = article.getContent().substring(0,
                content.length() > 100 ? 100 : content.length());
        article.setDescription(description);
        articleMapper.addArticle(article);
        return article;
    }

    @Override
    @CacheEvict(key = "#id")
    public Boolean delete(Long id) {
        return articleMapper.deleteById(id) > 0;
    }

    @Override
    @CacheEvict(key = "#ids")
    public void delete(@CacheAsMulti ArrayList<Long> ids) {
        Long author = StpUtil.getLoginIdAsLong();
        int lines = articleMapper.delete(new LambdaQueryWrapper<Article>().eq(Article::getAuthor, author).in(Article::getId, ids));
        if (!(lines > 0)) {
            throw new BusinessException("删除失败");
        }
    }

    @Override
    @CachePut(key = "#article.id")
    public Boolean update(Article article) {
        if (StringUtils.isAnyBlank(article.getTitle(), article.getContent())) {
            throw new BusinessException("标题和内容不能为空");
        }
        return articleMapper.updateById(article) > 0;
    }

    @Override
    @TrendCounter(key = "'count:article:trend:'+#id")
    @Cacheable(key = "#id")
    public ArticleVo getDetail(Long id) {
        try {
            Article article = articleMapper.selectById(id);
            UserVo userVo = userService.getUser(article.getAuthor());
            ArticleVo articleVo = articleVoMapper.toVo(article);
            articleVo.setUserVo(userVo);
            return articleVo;
        } catch (NullPointerException e) {
            throw new BusinessException("没有该文章", e);
        }

    }

    @Override
    public Article getById(Long id) {
        return articleMapper.selectById(id);
    }

    @Override
    public List<Article> searchByTag(String key) {
        return articleMapper.getByTags(key);
    }

    @Override
    public Page<Article> getPageList(int current, int size) {
        return articleMapper.selectPage(new Page<Article>(current, size)
                , new LambdaQueryWrapper<Article>()
                        .select(Article::getId)
                        .select(Article::getTitle)
                        .select(Article::getAuthor)
                        .select(Article::getViews)
                        .select(Article::getCreateTime)
        );
    }

    @Override
    public List<ArticleVo> getPageList(int page) {
        if (page < 0) {
            throw new BusinessException("页码不应小于0");
        }
        int size = 15;
        return articleMapper.getLimited((page - 1) * size, size);
    }

    @Override
    public List<ArticleVo> fuzzySearch(String keyword, int page) {
        int size = 15;
        page = page > 0 ? page : 0;
        int current = (page - 1) * size;
        return articleMapper.getByKeyword(keyword, current, size);
    }

    @Override
    public int getSearchCount(String keyword) {
        return articleMapper.getCountByKeyword(keyword);
    }

    @Override
    public int getRecordCount() {
        return articleMapper.getCount();
    }

    @Override
    @Cacheable(cacheNames = "article:user", key = "#id")
    public Page<Article> getUserList(int page, Long id) {
        page = page > 0 ? page : 0;
        int size = 15;
        return articleMapper.selectPage(new Page<Article>((page - 1) * size, size)
                , new LambdaQueryWrapper<Article>()
                        .select(Article::getId, Article::getTitle, Article::getViews, Article::getLikeNum)
                        .eq(Article::getStatus, Status.ACTIVE.getCode()));
    }

    @Override
    public Page<ArticleVo> getUserPage(int page) {
        return null;
    }

    @Override
    @Cacheable(cacheNames = "trend:article#7200")
    public List<ArticleVo> getTrend() {
        ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
        String keyFormat = "count:article:trend:";
        Set<String> keys = redisTemplate.keys(keyFormat + "*");
        if (keys.size() < 1) {
            throw new BusinessException("还未产生热点信息");
        }
        Map<Long, Integer> trends = new HashMap<>();
        keys.forEach(key -> {
            trends.put(Long.parseLong(key.substring(key.lastIndexOf(":") + 1)), operations.get(key));
        });
        List<Map.Entry<Long, Integer>> entryList = new ArrayList<>(trends.entrySet());
        entryList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        List<Long> ids = entryList.stream().map(entry -> {
            return entry.getKey();
        }).limit(10).collect(Collectors.toList());
        List<Article> articleList = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getTitle)
                .in(Article::getId, ids));
        articleList.sort(Comparator.comparingInt(article -> ids.indexOf(article.getId())));
        List<ArticleVo> articleVos = articleList.stream().map(article -> {
            ArticleVo articleVo = new ArticleVo();
            articleVo.setId(article.getId());
            articleVo.setTitle(article.getTitle());
            articleVo.setTrend((Integer) operations.get(keyFormat + article.getId()));
            return articleVo;
        }).collect(Collectors.toList());
        return articleVos;
    }

    @Override
    public void addView(Long id) {
        String key = "article:view:" + id;
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.increment(key);
        key = "article:" + id;
        ArticleVo articleVo = (ArticleVo) operations.get(key);
        articleVo.setViews(articleVo.getViews() + 1);
        operations.set(key, articleVo);
    }

    @Override
    public void like(ThumbsUp thumbs) {
        thumbs.setCreateTime(new Timestamp(System.currentTimeMillis()));
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps("like:article");
        String key = thumbs.getToId() + ":" + thumbs.getFromId();
        operations.delete(key + ":0");
        key = key + ":1";
        boolean flag = operations.putIfAbsent(key, thumbs);
        if (!flag) {
            throw new BusinessException("不能重复点赞");
        }
        key = "article:count:like:" + thumbs.getToId();
        redisTemplate.opsForValue().increment(key, 1);
        updateCacheLike(thumbs.getToId(), 1);
    }

    @Override
    public void cancelLike(ThumbsUp thumbs) {
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps("like:article");
        String key = thumbs.getToId() + ":" + thumbs.getFromId();
        operations.delete(key + ":1");
        key = key + ":0";
        boolean flag = operations.putIfAbsent(key, thumbs);
        if (!flag) {
            throw new BusinessException("已取消点赞，请勿重复请求");
        }
        key = "article:count:like:" + thumbs.getToId();
        redisTemplate.opsForValue().increment(key, -1);
        updateCacheLike(thumbs.getToId(), -1);
    }

    @Override
    public void updateCacheLike(Long id, Integer delta) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        String key = "article:" + id;
        ArticleVo articleVo = (ArticleVo) operations.get(key);
        if (Objects.nonNull(articleVo)) {
            articleVo.setLikeNum(articleVo.getLikeNum() + delta);
            operations.set(key, articleVo);
        } else {
            String info = String.format("缓存中暂时没有id为{%s}的文章", id);
            log.info(info);
        }
    }

    @Override
    public void updateDBLikeNum(Long articleId) {
        ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
        String key = "article:count:like:" + articleId;
        Integer count = operations.get(key);
        articleMapper.updateLikeNum(articleId, count);
        redisTemplate.delete(key);
    }

    @Override
    @Transactional
    //该方法注解式事务会失效
    public void saveLikeToDB() {
        BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps("like:article");
        //文章点赞的key格式为like:article，文章id:点赞人id:标识，标识为1即是点赞状态，标识为0即取消点赞状态
        //保持的信息中，点赞状态的有保存createtime，反之没有
        long begin = System.currentTimeMillis();
        Set<String> allKey = operations.keys();
        List<ThumbsUp> like = allKey.stream().filter(key -> '1' == key.charAt(key.length() - 1)).map(key -> {
            return (ThumbsUp) operations.get(key);
        }).collect(Collectors.toList());
        List<ThumbsUp> cancel = allKey.stream().filter(key -> '0' == key.charAt(key.length() - 1)).map(key -> {
            return (ThumbsUp) operations.get(key);
        }).collect(Collectors.toList());
        System.out.println("筛选耗时：" + (System.currentTimeMillis() - begin));
        begin = System.currentTimeMillis();
        //更新点赞数
        allKey.stream().forEach(key -> {
            Long id = Long.valueOf(key.substring(0, key.indexOf(":")));
            updateDBLikeNum(id);
            operations.delete(key);
        });
        System.out.println("删除缓存耗时:" + (System.currentTimeMillis() - begin));
        //更新点赞表信息
        if (like.size() > 0) {
            articleMapper.addLike(like);
        }
        if (cancel.size() > 0) {
            articleMapper.delLike(cancel);
        }

    }

    @Override
    public boolean getIsLiked(Long userId, Long articleId) {
        Object obj = redisTemplate.boundHashOps("like:article").get(articleId + ":" + userId + ":1");
        if (obj != null) {
            return true;
        }
        return articleMapper.getIsLikeInt(userId, articleId) > 0;
    }

    @Override
    public void updateCommentNum() {
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        redisTemplate.keys("count:article:comment:*").stream()
                .forEach(key -> {
                    String k = String.valueOf(key);
                    Long articleId = Long.valueOf((String) operations.get(k, "articleId"));
                    int num = (int) operations.get(k, "count");
                    articleMapper.updateCommentNum(articleId, num);
                    redisTemplate.delete(k);
                });
    }

    @Override
    public void updateDBView() {
        ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
        String keyFormat = "article:view:";
        log.info("article update view to db start------");
        redisTemplate.keys(keyFormat + "*")
                .stream()
                .forEach(key -> {
                    String temp = String.valueOf(key);
                    Long id = Long.parseLong(temp.substring(temp.lastIndexOf(":") + 1));
                    log.info("id:{}", id);
                    articleMapper.updateViewNum(id, operations.get(key));
                    redisTemplate.delete(key);
                });
        log.info("------article update view to db end");
    }
}
