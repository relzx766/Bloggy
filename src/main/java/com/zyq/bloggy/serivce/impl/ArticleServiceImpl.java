package com.zyq.bloggy.serivce.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.annotation.Statistics;
import com.zyq.bloggy.annotation.TaskInfo;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.mapStruct.ArticleVoMapper;
import com.zyq.bloggy.mapper.ArticleMapper;
import com.zyq.bloggy.model.entity.Status;
import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.model.pojo.Article;
import com.zyq.bloggy.model.vo.ArticleVo;
import com.zyq.bloggy.serivce.ArticleService;
import com.zyq.bloggy.serivce.RedisService;
import com.zyq.bloggy.serivce.TagService;
import com.zyq.bloggy.serivce.UserService;
import com.zyq.bloggy.util.StringUtils;
import com.zyq.bloggy.util.TimeUtil;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@CacheConfig(cacheNames = "article#600")
public class ArticleServiceImpl implements ArticleService {
    private static final String KEY_ARTICLE_LIKE_COUNT = "count:like:article";
    private static final String KEY_ARTICLE_LIKE = "like:article";
    private static final String KEY_ARTICLE_COMMENT_COUNT = "count:comment:article";
    private static final String KEY_ARTICLE_VIEW_COUNT = "count:article:view";
    private static final String KEY_ARTICLE_TREND_COUNT = "count:article:trend";
    private static final String KEY_ARTICLE_TREND = "trend:article";
    private static final String KEY_ARTICLE_IS_LIKE = "isLike:article";
    private static final String KEY_ADMIN_ARTICLE_PAGE = "admin:article:page#30";
    private static final String KEY_ARTICLE_ACTIVE_COUNT = "count:article:active#60";
    private final int DESCRIPTION_LENGTH = 100;
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    UserService userService;
    @Autowired
    TagService tagService;
    @Autowired
    ArticleVoMapper articleVoMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RedisService redisService;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    public Article publish(Article article) {
        if (StringUtils.isAnyBlank(article.getTitle(), article.getContent())) {
            throw new BusinessException("标题和内容不能为空");
        }
        article.setId(IdWorker.getId());
        article.setAuthor(StpUtil.getLoginIdAsLong());
        article.setLikeNum(0);
        if (Objects.isNull(article.getTags())) {
            article.setTags(new String[0]);
        }
        article.setViews(0);
        article.setComments(0);
        article.setStatus(Status.ACTIVE.getCode());
        article.setCreateTime(new Timestamp(System.currentTimeMillis()));
        article.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        article.setDescription(StringUtils.cutString(StringUtils.filterMd(StringUtils.replaceImage(article.getContent())), DESCRIPTION_LENGTH));
        articleMapper.addArticle(article);
        return article;
    }

    @Override
    @CacheEvict(key = "#id")
    public Boolean delete(Long id, Long userId) {
        return articleMapper.delete(new LambdaQueryWrapper<Article>().eq(Article::getId, id).eq(Article::getAuthor, userId)) > 0;
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean delete(Long id) {
        return articleMapper.delete(new LambdaQueryWrapper<Article>().eq(Article::getId, id)) > 0;
    }

    @Override
    @CacheEvict(key = "#ids")
    public void delete(@CacheAsMulti ArrayList<Long> ids, Long userId) {
        int lines = articleMapper.delete(new LambdaQueryWrapper<Article>().eq(Article::getAuthor, userId).in(Article::getId, ids));
        if (!(lines > 0)) {
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public void active(Long id) {
        articleMapper.updateStatusById(id, Status.ACTIVE.getCode());
    }

    @Override
    @CachePut(key = "#article.id")
    public ArticleVo update(Article article, Long userId) {
        if (StringUtils.isAnyBlank(article.getTitle(), article.getContent())) {
            throw new BusinessException("标题和内容不能为空");
        }
        article.setDescription(StringUtils.cutString(StringUtils.filterMd(StringUtils.replaceImage(article.getContent())), DESCRIPTION_LENGTH));
        article.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        articleMapper.updateByUserId(article, userId);
        ArticleVo articleVo = articleVoMapper.toVo(article);
        articleVo.setUserVo(userService.getUser(userId));
        return articleVo;
    }

    @Override
    @Statistics(key = "'count:article:trend:'+#id", timeout = 3)
    @Cacheable(cacheNames = "article#60", key = "#id")
    public ArticleVo getDetail(Long id) {
        try {
            return articleMapper.getDetail(id);
        } catch (NullPointerException e) {
            throw new BusinessException("没有该文章", e);
        }

    }

    @Override
    public ArticleVo getDetailForAdmin(Long id) {
        return articleMapper.getDetailForAdmin(id);
    }

    @Override
    public Article getById(Long id) {
        return articleMapper.getById(id);
    }

    @Override
    public List<ArticleVo> getByIds(Long[] ids) {
        return articleMapper.getByIds(ids);
    }

    @Override
    public Map<String, Object> searchByTag(String[] tags, int page) {

        page = page < 1 ? 1 : page;
        int size = 15;
        int current = (page - 1) * size;
        Map<String, Object> data = new HashMap<>();
        data.put("articles", articleMapper.getByTags(tags, current, size));
        data.put("count", articleMapper.getCountByTags(tags));
        return data;
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
    @Cacheable(cacheNames = "article:page#60", key = "#page")
    public List<ArticleVo> getPageList(int page) {
        page = page < 1 ? 1 : page;
        int size = 15;
        return articleMapper.getLimited((page - 1) * size, size);
    }

    @Override
    @Cacheable(cacheNames = KEY_ADMIN_ARTICLE_PAGE, key = "#page")
    public List<ArticleVo> getPageListForAdmin(int page) {
        page = page < 1 ? 1 : page;
        int size = 15;
        return articleMapper.getArticleLimit((page - 1) * size, size);
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
    @Cacheable(cacheNames = KEY_ARTICLE_ACTIVE_COUNT)
    public int getActiveCount() {
        return Math.toIntExact(articleMapper.selectCount(new LambdaQueryWrapper<Article>().eq(Article::getStatus, Status.ACTIVE.getCode())));
    }

    @Override
    @Cacheable(cacheNames = "article:user#60", key = "#id")
    public Page<Article> getUserList(int page, Long id) {
        page = page > 0 ? page : 0;
        int size = 15;
        return articleMapper.selectPage(new Page<Article>((page - 1) * size, size)
                , new LambdaQueryWrapper<Article>()
                        .select(Article::getId, Article::getTitle, Article::getCreateTime)
                        .eq(Article::getStatus, Status.ACTIVE.getCode())
                        .eq(Article::getAuthor, id)
        );
    }

    @Override
    //根据年份获取用户发布过的文章
    public List<Article> getUserRecordsOfYear(long userId, int year) {
        long star = TimeUtil.getTimestampOfYearStart(year);
        long end = TimeUtil.getTimestampOfYearEnd(year);
        return articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getTitle, Article::getCreateTime)
                .eq(Article::getAuthor, userId)
                .eq(Article::getStatus, Status.ACTIVE.getCode())
                .between(Article::getCreateTime, new Timestamp(star), new Timestamp(end)));
    }

    //获取用户有记录的年份
    @Override
    @Cacheable(cacheNames = "article:user:year#3600", key = "#userId")
    public Set<String> getUserTimeline(long userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .select(Article::getCreateTime)
                .eq(Article::getAuthor, userId)
                .eq(Article::getStatus, Status.ACTIVE.getCode())).stream().map(article -> {
            Date date = new Date();
            date.setTime(article.getCreateTime().getTime());
            return dateFormat.format(date);
        }).collect(Collectors.toSet());
    }

    @Override
    public Page<ArticleVo> getUserPage(int page) {
        return null;
    }

    @Override
    @Async
    @Scheduled(cron = "0 */20 * * * *")
    @TaskInfo(group = "article", value = "update trend", description = "更新文章热门信息")
    public void updateTrend() {
        ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
        Set<String> keys = redisTemplate.keys(KEY_ARTICLE_TREND_COUNT + "*");
        if (keys.size() < 1) {
            log.info("还未产生热点信息");
            return;
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
            articleVo.setTrend((Integer) operations.get(KEY_ARTICLE_TREND_COUNT + ":" + article.getId()));
            return articleVo;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(KEY_ARTICLE_TREND, articleVos);
    }

    @Override
    public List<ArticleVo> getTrend() {
        return (List<ArticleVo>) redisTemplate.opsForValue().get(KEY_ARTICLE_TREND);
    }

    @Override
    public List<Map<String, Integer>> getCountRangeByDate(Integer day) {
        return articleMapper.getCountRangeByTime(TimeUtil.getTimestampRange(day));
    }

    @Override
    public void addView(Long id) {
        BoundHashOperations<String, String, Integer> boundHashOps = redisTemplate.boundHashOps(KEY_ARTICLE_VIEW_COUNT);
        boundHashOps.increment(String.valueOf(id), 1);
    }

    @Override
    public void like(ThumbsUp thumbs) {
        redisService.like(KEY_ARTICLE_LIKE, thumbs);
        updateCacheLike(thumbs.getToId(), 1);
    }

    @Override
    public void cancelLike(ThumbsUp thumbs) {
        redisService.cancelLike(KEY_ARTICLE_LIKE, thumbs);
        updateCacheLike(thumbs.getToId(), -1);
    }

    @Override
    public void updateCacheLike(Long id, Integer delta) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        String key = "article:" + id;
        ArticleVo articleVo = (ArticleVo) operations.get(key);
        long time = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (Objects.nonNull(articleVo)) {
            articleVo.setLikeNum(articleVo.getLikeNum() + delta);
            operations.set(key, articleVo, time, TimeUnit.SECONDS);
        } else {
            String info = String.format("缓存中暂时没有id为{%s}的文章", id);
            log.info(info);
        }
    }

    @Override
    public void updateDBLikeNum(long id, int num) {
        articleMapper.updateLikeNum(id, num);
    }

    @Override
    @Transactional
    @Async
    @Scheduled(cron = "0 */2 * * * *")
    @TaskInfo(group = "article", value = "update like", description = "更新文章点赞信息")
    public void saveLikeToDB() {
        Map<Long, List<ThumbsUp>> like = redisService.getLike(KEY_ARTICLE_LIKE);
        Map<Long, List<ThumbsUp>> cancel = redisService.getCancelLike(KEY_ARTICLE_LIKE);
        like.forEach((articleId, thumbs) -> {
            if (thumbs.size() > 0) {
                int countOfNewLike = articleMapper.addLike(thumbs);
                updateDBLikeNum(articleId, countOfNewLike);
            }
        });
        cancel.forEach((articleId, thumbs) -> {
            if (thumbs.size() > 0) {
                int countOfCancelLike = articleMapper.delLike(thumbs);
                updateDBLikeNum(articleId, -countOfCancelLike);
            }
        });
    }

    @Override
    @Cacheable(cacheNames = "isLike:article#60", key = "#articleId+':'+#userId")
    public boolean getIsLiked(Long userId, Long articleId) {
        return articleMapper.getIsLikeInt(userId, articleId) > 0;
    }

    @Override
    @Async
    @Scheduled(cron = "0 */3 * * * *")
    @TaskInfo(group = "article", value = "update comment number", description = "更新文章评论信息")
    public void updateCommentNum() {
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        redisTemplate.keys(KEY_ARTICLE_COMMENT_COUNT + ":*").stream()
                .forEach(key -> {
                    String k = String.valueOf(key);
                    Long articleId = Long.valueOf((String) operations.get(k, "articleId"));
                    int num = (int) operations.get(k, "count");
                    articleMapper.updateCommentNum(articleId, num);
                    redisTemplate.delete(key);
                });
    }

    @Override
    @Async
    @Scheduled(cron = "0 0 * * * *")
    @TaskInfo(group = "article", value = "decrement trend weight", description = "消减文章热度值")
    public void decrementTrend() {
        ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
        redisTemplate.keys(KEY_ARTICLE_TREND_COUNT + "*")
                .forEach(key -> {
                    if (operations.get(key) > 0) {
                        operations.increment((String) key, -1);
                    } else {
                        redisTemplate.delete(key);
                    }
                });
    }

    @Override
    @Async
    @Scheduled(cron = "0 */4 * * * *")
    @TaskInfo(group = "article", value = "update views", description = "更新文章点击信息")
    public void updateDBView() {
        BoundHashOperations<String, String, Integer> hashOperations = redisTemplate.boundHashOps(KEY_ARTICLE_VIEW_COUNT);

        hashOperations.keys().stream().forEach(key -> {
            articleMapper.updateViewNum(Long.valueOf(key), hashOperations.get(key));
            hashOperations.delete(key);
        });
    }
}
