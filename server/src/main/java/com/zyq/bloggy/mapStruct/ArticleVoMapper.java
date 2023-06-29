package com.zyq.bloggy.mapStruct;

import com.zyq.bloggy.model.pojo.Article;
import com.zyq.bloggy.model.vo.ArticleVo;
import com.zyq.bloggy.util.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ArticleVoMapper {
    ArticleVo toVo(Article article);

}
