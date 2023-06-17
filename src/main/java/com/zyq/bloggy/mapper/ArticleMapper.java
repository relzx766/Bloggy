package com.zyq.bloggy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyq.bloggy.pojo.Article;
import com.zyq.bloggy.pojo.ThumbsUp;
import com.zyq.bloggy.vo.ArticleVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {
    int addArticle(@Param("art") Article article);

    int delByIdsAndAuthor(@Param("userId") Long userId, @Param("ids") ArrayList<Long> ids);

    List<Article> getByTags(String tags);

    List<ArticleVo> getLimited(int current, int size);

    List<ArticleVo> getByKeyword(@Param("keyword") String keyword, @Param("current") int current, @Param("size") int size);

    int getCount();

    int getCountByKeyword(String keyword);

    int addLike(List<ThumbsUp> thumbs);

    int delLike(List<ThumbsUp> thumbs);

    int updateLikeNum(@Param("id") Long id, @Param("num") Integer num);

    int updateViewNum(@Param("id") Long id, @Param("num") Integer num);

    int getIsLikeInt(@Param("userId") Long userId, @Param("articleId") Long articleId);

    List<Article> getTrend(@Param("ids") List<Long> ids);

    int updateCommentNum(@Param("id") long id, @Param("num") int num);
}
