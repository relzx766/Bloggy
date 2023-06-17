package com.zyq.bloggy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyq.bloggy.pojo.User;
import com.zyq.bloggy.vo.UserStateVo;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
    UserStateVo getUserStateById(Long id);
}
