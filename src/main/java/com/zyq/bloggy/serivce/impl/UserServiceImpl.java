package com.zyq.bloggy.serivce.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.exception.ServiceException;
import com.zyq.bloggy.mapStruct.UserVoMapper;
import com.zyq.bloggy.mapper.UserMapper;
import com.zyq.bloggy.pojo.Code;
import com.zyq.bloggy.pojo.Role;
import com.zyq.bloggy.pojo.Status;
import com.zyq.bloggy.pojo.User;
import com.zyq.bloggy.serivce.UserService;
import com.zyq.bloggy.util.StringUtils;
import com.zyq.bloggy.vo.UserStateVo;
import com.zyq.bloggy.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@CacheConfig(cacheNames = "user")
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserVoMapper userVoMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public UserVo login(String account, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPassword, DigestUtils.md5Hex(password)).eq(User::getStatus, Status.ACTIVE.getCode());
        if ("@".indexOf(account) != -1) {
            wrapper.eq(User::getEmail, account);
        } else {
            wrapper.eq(User::getUsername, account);
        }
        return userVoMapper.toVO(userMapper.selectOne(wrapper));
    }

    @Override
    public Boolean closeAccount(Long id) {
        return userMapper.update(null, new LambdaUpdateWrapper<User>().eq(User::getId, id).set(User::getStatus, Status.INACTIVE.getCode())) > 0;
    }

    @Override
    public User updateProfile(User user) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if (!user.getNickname().isEmpty()) {
            lambdaUpdateWrapper.set(User::getNickname, user.getNickname());
        }
        if (!user.getAvatar().isEmpty()) {
            lambdaUpdateWrapper.set(User::getAvatar, user.getAvatar());
        }
        userMapper.update(user, lambdaUpdateWrapper.eq(User::getId, userId));
        return user;
    }

    @Override
    @CachePut(cacheNames = "reg", key = "#user.email")
    public User regBeforeVerify(User user) {
        if (StringUtils.isAnyBlank(user.getUsername(), user.getPassword(), user.getEmail())) {
            throw new BusinessException("请先完善信息");
        }
        if (checkUsernameIsUsed(user.getUsername()) || checkEmailIsUsed(user.getEmail())) {
            throw new BusinessException("该用户名或邮箱已被使用，请更换");
        }
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        return user;
    }

    @Override
    @CacheEvict(cacheNames = "reg", key = "#email")
    public Boolean register(String email) {
        User user = (User) redisTemplate.opsForValue().get("reg:" + email);
        if (StringUtils.isAnyBlank(user.getUsername(), user.getPassword(), user.getEmail())) {
            throw new ServiceException(Code.SYSTEM_ERROR.getCode(), "系统缓存出现错误");
        }
        if (Objects.isNull(user.getRole())) {
            user.setRole(Role.MEMBER.getCode());
        }
        user.setNickname("bloggy用户#" + user.getUsername());
        user.setAvatar("http://localhost:8080/default/logo.svg");
        user.setStatus(Status.ACTIVE.getCode());
        user.setRegistrationTime(new Timestamp(System.currentTimeMillis()));
        return userMapper.insert(user) > 0;
    }

    @Override
    public Boolean activeAccount(Long id) {
        return userMapper.update(null, new LambdaUpdateWrapper<User>().eq(User::getId, id).set(User::getStatus, Status.ACTIVE.getCode())) > 0;
    }

    @Override
    public UserVo getUser(Long id) {
        return userVoMapper.toVO(userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getId, id)
                        .eq(User::getStatus, Status.ACTIVE.getCode())));
    }

    @Override
    public User getUserDetail(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> getUser(List<Long> ids) {
        return userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getId, ids));
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
    }

    @Override
    public List<UserVo> getUser(String account) {
        List<User> userList = new ArrayList<>();
        userList = userMapper.selectList(new LambdaQueryWrapper<User>()
                .like(User::getUsername, account)
                .or()
                .like(User::getNickname, account)
                .eq(User::getStatus, Status.ACTIVE)
        );
        List<UserVo> userVos = new ArrayList<>();
        userList.forEach(user -> userVos.add(userVoMapper.toVO(user)));
        return userVos;
    }

    @Override
    public Page<UserVo> getUser(Integer page) {
        if (page < 0) {
            throw new BusinessException("数字不合法,页数不可小于0");
        }
        int pageSize = 15;
        int current = (page - 1) * pageSize;
        int size = page * pageSize;
        Page<User> userPage = userMapper.selectPage(new Page<User>(current, size), null);
        return (Page<UserVo>) userPage.convert(user -> userVoMapper.toVO(user));
    }

    @Override
    public Integer getRole(Long id) {
        return userMapper.selectById(id).getRole();
    }

    @Override
    public UserStateVo getUserStats(Long id) {
        return userMapper.getUserStateById(id);
    }

    @Override
    public boolean checkUsernameIsUsed(String username) {
        return Objects.nonNull(userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username)));
    }

    @Override
    public boolean checkEmailIsUsed(String email) {
        return Objects.nonNull(userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email)));
    }
}
