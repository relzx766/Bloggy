package com.zyq.bloggy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyq.bloggy.model.pojo.User;
import com.zyq.bloggy.model.vo.UserStateVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from tb_user limit #{current},#{size}")
    List<User> getUserList(@Param("current") int current, @Param("size") int size);
}
