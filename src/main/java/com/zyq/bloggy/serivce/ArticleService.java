package com.zyq.bloggy.serivce;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.pojo.Article;
import com.zyq.bloggy.pojo.ThumbsUp;
import com.zyq.bloggy.vo.ArticleVo;

import java.util.ArrayList;
import java.util.List;

public interface ArticleService {
    Article publish(Article article);

    Boolean delete(Long id);

    void delete(ArrayList<Long> ids);

    //需要设置一个redis定时任务更新文章的阅读量和点赞量
    Boolean update(Article article);

    ArticleVo getDetail(Long id);

    Article getById(Long id);

    List<Article> searchByTag(String key);

    @Deprecated
    Page<Article> getPageList(int offset, int end);

    List<ArticleVo> getPageList(int page);

    List<ArticleVo> fuzzySearch(String keyword, int page);

    int getSearchCount(String keyword);

    int getRecordCount();

    //获取指定用户的作品分页
    Page<Article> getUserList(int page, Long id);

    @Deprecated
    Page<ArticleVo> getUserPage(int page);

    List<ArticleVo> getTrend();

    void addView(Long id);

    void like(ThumbsUp thumbs);

    void cancelLike(ThumbsUp thumbs);

    void updateCacheLike(Long id, Integer delta);

    void saveLikeToDB();

    void updateDBLikeNum(Long articleId);

    void updateDBView();

    boolean getIsLiked(Long userId, Long articleId);

    void updateCommentNum();

}
