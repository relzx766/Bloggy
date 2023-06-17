package com.zyq.bloggy.mapStruct;

import com.zyq.bloggy.pojo.Article;
import com.zyq.bloggy.vo.ArticleVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ArticleVoMapper {
    @Mapping(target = "tags", source = "tags", qualifiedByName = "replace")
    ArticleVo toVo(Article article);

    @Named("replace")
    default String replace(String str) {
        return str.replace("\"", "");
    }
}
