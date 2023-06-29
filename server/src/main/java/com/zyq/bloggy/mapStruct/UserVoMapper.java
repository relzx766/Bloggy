package com.zyq.bloggy.mapStruct;

import com.zyq.bloggy.model.pojo.User;
import com.zyq.bloggy.model.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserVoMapper {
    String[] roles = {
            "admin",
            "member",
            "visitor"
    };

    @Mapping(source = "role", target = "role", qualifiedByName = "mapRole")
    UserVo toVO(User user);

    @Named("mapRole")
    default String mapRole(int role) {
        return roles[role];
    }
}
