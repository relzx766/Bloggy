package com.zyq.bloggy.serivce;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.pojo.Tag;

import java.util.ArrayList;
import java.util.List;

public interface TagService {
    Boolean addTag(Tag tag);

    Boolean delTag(Integer id);

    Boolean delTag(ArrayList<Integer> id);

    Tag getTag(Integer id);

    List<Tag> getList(ArrayList<Integer> id);

    List<Tag> getList(String content);

    Page<Tag> getPage(int offset, int end);

    Boolean update(Tag tag);
}
