package com.zyq.bloggy.serivce.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.mapper.SortMapper;
import com.zyq.bloggy.model.entity.Status;
import com.zyq.bloggy.model.pojo.Sort;
import com.zyq.bloggy.model.vo.ArticleVo;
import com.zyq.bloggy.serivce.ArticleService;
import com.zyq.bloggy.serivce.SortService;
import com.zyq.bloggy.util.StringUtils;
import io.github.ms100.cacheasmulti.cache.annotation.CacheAsMulti;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@CacheConfig(cacheNames = "sort")
public class SortServiceImpl implements SortService {
    private static final String DEFAULT_COVER = "http://localhost:8080/default/cover.png";
    @Autowired
    SortMapper sortMapper;
    @Autowired
    ArticleService articleService;

    @Override
    public Sort addSort(Sort sort) {
        if (StringUtils.isBlank(sort.getTitle())) {
            sort.setTitle("默认标题");
        }
        if (StringUtils.isBlank(sort.getCover())) {
            sort.setCover(DEFAULT_COVER);
        }
        sort.setOwner(StpUtil.getLoginIdAsLong());
        sort.setStatus(Status.ACTIVE.getCode());
        sort.setCreateTime(new Timestamp(System.currentTimeMillis()));
        sortMapper.insert(sort);
        return sort;
    }

    @Override
    public Boolean delSort(Integer id) {
        Long userId = StpUtil.getLoginIdAsLong();
        return sortMapper.delete(new LambdaUpdateWrapper<Sort>()
                .eq(Sort::getId, id)
                .eq(Sort::getOwner, userId)) > 0;
    }

    @Override
    public Boolean updateSort(Sort sort) {
        LambdaUpdateWrapper<Sort> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Sort::getId, sort.getId())
                .eq(Sort::getOwner, sort.getOwner())
                .eq(Sort::getStatus, Status.ACTIVE.getCode());
        if (!StringUtils.isBlank(sort.getTitle())) {
            wrapper.set(Sort::getTitle, sort.getTitle());
        }
        if (!StringUtils.isBlank(sort.getCover())) {
            wrapper.set(Sort::getCover, sort.getCover());

        }
        return sortMapper.update(null, wrapper) > 0;
    }

    @Override
    public Map<String, Object> getDetail(Integer id) {
        Map<String, Object> data = new HashMap<>();
        Sort sort = sortMapper.selectById(id);
        Long[] ids = sortMapper.getArticleIdsBySortId(id);
        List<ArticleVo> articleList = articleService.getByIds(ids);
        data.put("sort", sort);
        data.put("articles", articleList);
        return data;
    }

    @Override
    public List<Sort> getUserSort(Long id) {
        return sortMapper.selectList(new LambdaQueryWrapper<Sort>()
                .eq(Sort::getOwner, id)
                .eq(Sort::getStatus, Status.ACTIVE.getCode()));
    }

    @Override
    public Page<Sort> getSortPage(int current, int size) {
        return sortMapper.selectPage(new Page<Sort>(current, size), null);
    }

    @Override
    public boolean addArticle(Integer sortId, Long articleId, long userId) {
        if (Objects.isNull(sortId) || Objects.isNull(articleId)) {
            throw new BusinessException("参数不全");
        }
        return sortMapper.addArticleToSort(sortId, articleId, userId) > 0;
    }

    @Override
    public void delArticle(Integer sortId, Long articleId, long userId) {
        if (Objects.isNull(sortId) || Objects.isNull(articleId)) {
            throw new BusinessException("参数不全");
        }
        if (userId != sortMapper.selectById(sortId).getOwner()) {
            throw new BusinessException("你不是该收藏夹的拥有者");
        }
        sortMapper.delArticleFromSort(sortId, articleId);

    }

    @Override
    public void delArticle(Integer sortId, @CacheAsMulti Long[] articleIds) {
        sortMapper.delArticlesFromSort(sortId, articleIds);
    }

    @Override
    public boolean getArticleIsSort(long articleId, long userId) {
        return sortMapper.getSortedByUserIdAndArticleId(articleId, userId) > 0;
    }

    @Override
    public void cancelSort(long articleId, long userId) {
        sortMapper.deleteByArticleIdAndUSerId(articleId, userId);
    }
}
