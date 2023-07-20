package com.zyq.bloggy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyq.bloggy.model.pojo.Sort;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SortMapper extends BaseMapper<Sort> {
    int addArticleToSort(@Param("sortId") int sortId, @Param("articleId") long articleId, @Param("userId") long userId);


    int delById(@Param("id") Long id, @Param("userId") Long owner);

    int delArticleFromSort(@Param("sortId") int sortId, @Param("articleId") long articleId);

    int delArticlesFromSort(@Param("sortId") int sortId, @Param("articleIds") Long[] articleIds);

    int getSortedByUserIdAndArticleId(@Param("articleId") long articleId, @Param("userId") long userId);

    int deleteByArticleIdAndUSerId(@Param("articleId") long articleId, @Param("userId") long userId);

    Long[] getArticleIdsBySortId(int id);
}
