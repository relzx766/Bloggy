package com.zyq.bloggy.serivce.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.annotation.TrendCounter;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.mapper.SortMapper;
import com.zyq.bloggy.model.pojo.Sort;
import com.zyq.bloggy.model.entity.Status;
import com.zyq.bloggy.serivce.SortService;
import io.github.ms100.cacheasmulti.cache.annotation.CacheAsMulti;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@CacheConfig(cacheNames = "sort")
public class SortServiceImpl implements SortService {
    @Autowired
    SortMapper sortMapper;
    private static final String DEFAULT_COVER = "http://localhost:8080/default/cover.png";

    @Override
    public Sort addSort(Sort sort) {
        if (sort.getTitle().isEmpty()) {
            sort.setTitle("默认标题");
        }
        if (sort.getCover().isEmpty()) {
            sort.setCover(DEFAULT_COVER);
        }
        sort.setOwner(StpUtil.getLoginIdAsLong());
        sort.setStatus(Status.INACTIVE.getCode());
        sort.setCreateTime(new Timestamp(System.currentTimeMillis()));
        sortMapper.insert(sort);
        return sort;
    }

    @Override
    @CacheEvict(key = "#id")
    public Boolean delSort(Integer id) {
        Long userId = StpUtil.getLoginIdAsLong();
        return sortMapper.delete(new LambdaUpdateWrapper<Sort>()
                .eq(Sort::getId, id)
                .eq(Sort::getOwner, userId)) > 0;
    }

    @Override
    @CachePut(key = "sort.id")
    public Boolean updateSort(Sort sort) {
        Long userId = StpUtil.getLoginIdAsLong();
        return sortMapper.update(null, new LambdaUpdateWrapper<Sort>()
                .set(Sort::getTitle, sort.getTitle())
                .set(Sort::getCover, sort.getCover())
                .eq(Sort::getId, sort.getId())
                .eq(Sort::getOwner, userId)
                .eq(Sort::getStatus, Status.ACTIVE.getCode())) > 0;
    }

    @Override
    @TrendCounter(key = "sort")
    @Cacheable(key = "#id")
    public Sort getDetail(Integer id) {
        return sortMapper.getDetailById(id);
    }

    @Override
    @TrendCounter(key = "sort")
    @Cacheable(cacheNames = "sort::user", key = "#id")
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
    @CachePut(key = "#sortId")
    public Sort addArticle(Integer sortId, Long articleId) {
        if (Objects.isNull(sortId) || Objects.isNull(articleId)) {
            throw new BusinessException("参数不全");
        }
        if (sortMapper.addArticleToSort(sortId, articleId) > 0) {
            return sortMapper.getDetailById(sortId);
        } else {
            return null;
        }
    }

    @Override
    @CacheEvict
    public Sort delArticle(Integer sortId, Long articleId) {
        if (Objects.isNull(sortId) || Objects.isNull(articleId)) {
            throw new BusinessException("参数不全");
        }
        if (sortMapper.delArticleFromSort(sortId, articleId) > 0) {
            return sortMapper.getDetailById(sortId);
        }
        return null;
    }

    @Override
    @CacheEvict(key = "#articleIds")
    public Sort delArticle(Integer sortId, @CacheAsMulti Long[] articleIds) {
        if (sortMapper.delArticlesFromSort(sortId, articleIds) > 0) {
            return sortMapper.getDetailById(sortId);
        }
        return null;
    }
}
