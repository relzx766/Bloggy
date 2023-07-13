package com.zyq.bloggy.serivce;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.model.pojo.Sort;

import java.util.List;
import java.util.Map;

public interface SortService {
    Sort addSort(Sort sort);

    Boolean delSort(Integer id);

    Boolean updateSort(Sort sort);

    Map<String, Object> getDetail(Integer id);

    List<Sort> getUserSort(Long id);

    Page<Sort> getSortPage(int offset, int end);

    boolean addArticle(Integer sortId, Long articleId, long userId);

    void delArticle(Integer sortId, Long articleId, long userId);

    void delArticle(Integer sortId, Long[] articleIds);

    boolean getArticleIsSort(long articleId, long userId);

    void cancelSort(long articleId, long userId);
}
