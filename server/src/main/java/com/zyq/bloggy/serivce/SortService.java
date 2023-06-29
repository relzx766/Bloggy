package com.zyq.bloggy.serivce;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.model.pojo.Sort;

import java.util.List;

public interface SortService {
    Sort addSort(Sort sort);

    Boolean delSort(Integer id);

    Boolean updateSort(Sort sort);

    Sort getDetail(Integer id);

    List<Sort> getUserSort(Long id);

    Page<Sort> getSortPage(int offset, int end);

    Sort addArticle(Integer sortId, Long articleId);

    Sort delArticle(Integer sortId, Long articleId);

    Sort delArticle(Integer sortId, Long[] articleIds);
}
