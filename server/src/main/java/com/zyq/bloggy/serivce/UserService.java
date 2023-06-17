package com.zyq.bloggy.serivce;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.pojo.User;
import com.zyq.bloggy.vo.UserStateVo;
import com.zyq.bloggy.vo.UserVo;

import java.util.List;

public interface UserService {
    UserVo login(String account, String password);

    Boolean closeAccount(Long id);

    User updateProfile(User user);

    //注册，邮箱验证前先写入缓存
    User regBeforeVerify(User user);

    Boolean register(String email);

    Boolean activeAccount(Long id);

    UserVo getUser(Long id);

    User getUserDetail(Long id);

    List<User> getUser(List<Long> ids);

    User getUserByEmail(String email);

    //根据用户名，昵称获取
    List<UserVo> getUser(String account);

    Page<UserVo> getUser(Integer page);

    Integer getRole(Long id);

    UserStateVo getUserStats(Long id);

    boolean checkUsernameIsUsed(String username);

    boolean checkEmailIsUsed(String email);
}
