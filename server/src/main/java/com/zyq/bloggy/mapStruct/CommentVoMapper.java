package com.zyq.bloggy.mapStruct;

import com.zyq.bloggy.model.pojo.ArticleComment;
import com.zyq.bloggy.model.pojo.ReplyComment;
import com.zyq.bloggy.model.pojo.User;
import com.zyq.bloggy.model.vo.CommentVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CommentVoMapper {
    @Mapping(source = "articleComment.id", target = "commentId", qualifiedByName = "longToStr")
    CommentVo toVo(ArticleComment articleComment);

    @Mapping(source = "replyComment.id", target = "replyId", qualifiedByName = "longToStr")
    @Mapping(source = "replyComment.commentId", target = "commentId", qualifiedByName = "longToStr")
    CommentVo toVo(ReplyComment replyComment);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "articleComment.id", target = "commentId", qualifiedByName = "longToStr")
    CommentVo toVo(User user, ArticleComment articleComment);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "replyComment.id", target = "replyId", qualifiedByName = "longToStr")
    @Mapping(source = "replyComment.commentId", target = "commentId", qualifiedByName = "longToStr")
    CommentVo toVo(User user, ReplyComment replyComment);

    @Named("longToStr")
    default String longToStr(Long id) {
        return String.valueOf(id);
    }

}
