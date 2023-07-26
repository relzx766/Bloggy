package com.zyq.bloggy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyq.bloggy.model.pojo.User;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from tb_user limit #{current},#{size}")
    List<User> getUserList(@Param("current") int current, @Param("size") int size);

    @Select("select count(id) from tb_user")
    int getCount();

    @MapKey("date")
    List<Map<String, Integer>> getCountByTime(Timestamp time);

}
