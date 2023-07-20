package com.zyq.bloggy.serivce;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.model.pojo.Article;
import com.zyq.bloggy.model.vo.ArticleVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ArticleService {
    Article publish(Article article);

    Boolean delete(Long id, Long userId);

    boolean delete(Long id);

    void delete(ArrayList<Long> ids, Long userId);

    void active(Long id);

    //需要设置一个redis定时任务更新文章的阅读量和点赞量
    ArticleVo update(Article article, Long userId);

    ArticleVo getDetail(Long id);

    ArticleVo getDetailForAdmin(Long id);

    Article getById(Long id);

    List<ArticleVo> getByIds(Long[] ids);

    /**
     * @param tags
     * @param page
     * @return articles:List<articleVo> count:符合条件记录的数量
     */
    Map<String, Object> searchByTag(String[] tags, int page);

    @Deprecated
    Page<Article> getPageList(int offset, int end);

    List<ArticleVo> getPageList(int page);

    List<ArticleVo> getPageListForAdmin(int page);


    List<ArticleVo> fuzzySearch(String keyword, int page);

    int getSearchCount(String keyword);

    int getRecordCount();

    int getActiveCount();

    //获取指定用户的作品分页
    Page<Article> getUserList(int page, Long id);

    List<Article> getUserRecordsOfYear(long userId, int year);

    Set<String> getUserTimeline(long userId);

    @Deprecated
    Page<ArticleVo> getUserPage(int page);


    List<ArticleVo> getTrend();

    List<Map<String, Integer>> getCountRangeByDate(Integer day);

    void addView(Long id);

    void like(ThumbsUp thumbs);

    void cancelLike(ThumbsUp thumbs);

    boolean getIsLiked(Long userId, Long articleId);

    void updateCacheLike(Long id, Integer delta);
    //以下都为定时任务

    void saveLikeToDB();

    void updateDBLikeNum(long id, int num);

    void updateDBView();


    void updateCommentNum();

    void decrementTrend();

    void updateTrend();

}
