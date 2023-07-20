package com.zyq.bloggy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyq.bloggy.model.entity.ThumbsUp;
import com.zyq.bloggy.model.pojo.Article;
import com.zyq.bloggy.model.vo.ArticleVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {
    Article getById(long id);

    int updateByUserId(@Param("article") Article article, @Param("userId") long userId);

    int updateStatusById(@Param("id") long id, @Param("status") int status);

    ArticleVo getDetail(@Param("articleId") long articleId);

    ArticleVo getDetailForAdmin(@Param("articleId") long articleId);

    int addArticle(@Param("art") Article article);

    int delByIdsAndAuthor(@Param("userId") Long userId, @Param("ids") ArrayList<Long> ids);

    ArrayList<ArticleVo> getByTags(@Param("tags") String[] tags, @Param("current") int current, @Param("size") int size);

    int getCountByTags(@Param("tags") String[] tags);

    List<ArticleVo> getLimited(int current, int size);

    List<ArticleVo> getByIds(Long[] ids);

    List<ArticleVo> getArticleLimit(int current, int size);

    List<ArticleVo> getByKeyword(@Param("keyword") String keyword, @Param("current") int current, @Param("size") int size);

    int getCount();

    int getCountByKeyword(String keyword);

    int addLike(List<ThumbsUp> thumbs);

    int getLike(@Param("userId") long userId, @Param("articleId") long articleId);

    int delLike(List<ThumbsUp> thumbs);

    int updateLikeNum(@Param("id") Long id, @Param("num") Integer num);

    int updateViewNum(@Param("id") Long id, @Param("num") Integer num);

    int getIsLikeInt(@Param("userId") Long userId, @Param("articleId") Long articleId);

    List<Article> getTrend(@Param("ids") List<Long> ids);

    int updateCommentNum(@Param("id") long id, @Param("num") int num);

    @MapKey("date")
    List<Map<String, Integer>> getCountRangeByTime(Timestamp time);
}
