package com.zyq.bloggy.serivce.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.mapper.TagMapper;
import com.zyq.bloggy.model.entity.Status;
import com.zyq.bloggy.model.pojo.Tag;
import com.zyq.bloggy.serivce.TagService;
import io.github.ms100.cacheasmulti.cache.annotation.CacheAsMulti;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = "tag")
public class TagServiceImpl implements TagService {
    @Autowired
    TagMapper tagMapper;

    @Override
    public Boolean addTag(Tag tag) {
        return tagMapper.insert(tag) > 0;
    }

    @Override
    public Boolean delTag(Integer id) {
        return tagMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean delTag(ArrayList<Integer> id) {
        return tagMapper.deleteBatchIds(id) == id.size();
    }

    @Override
    public Tag getTag(Integer id) {
        return tagMapper.selectById(id);
    }

    @Override
    @Cacheable(key = "#id")
    public List<Tag> getList(@CacheAsMulti ArrayList<Integer> id) {
        return tagMapper.selectBatchIds(id);
    }

    @Override
    @Cacheable(key = "#content")
    public List<Tag> getList(String content) {
        return tagMapper.selectList(new LambdaQueryWrapper<Tag>().like(Tag::getContent, content).eq(Tag::getStatus, Status.ACTIVE.getCode()));
    }

    @Override
    public Page<Tag> getPage(int current, int size) {
        return tagMapper.selectPage(new Page<Tag>(current, size), null);
    }

    @Override
    public List<Tag> searchTag(String keyword) {
        return tagMapper.selectList(new LambdaQueryWrapper<Tag>().like(Tag::getContent, keyword));
    }

    @Override
    public Boolean update(Tag tag) {
        return tagMapper.updateById(tag) > 0;
    }
}
