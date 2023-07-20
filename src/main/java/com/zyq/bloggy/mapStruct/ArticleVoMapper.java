package com.zyq.bloggy.mapStruct;

import com.zyq.bloggy.model.pojo.Article;
import com.zyq.bloggy.model.vo.ArticleVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface ArticleVoMapper {
    @Mapping(target = "tags", source = "tags", qualifiedByName = "setTags")
    ArticleVo toVo(Article article);

    @Named("setTags")
    default String[] setTags(String[] tags) {
        if (Objects.isNull(tags)) {
            return new String[0];
        }
        return tags;
    }
}
