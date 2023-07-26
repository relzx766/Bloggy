package com.zyq.bloggy.serivce.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.exception.ServiceException;
import com.zyq.bloggy.mapStruct.UserVoMapper;
import com.zyq.bloggy.mapper.UserMapper;
import com.zyq.bloggy.model.entity.Code;
import com.zyq.bloggy.model.entity.Role;
import com.zyq.bloggy.model.entity.Status;
import com.zyq.bloggy.model.pojo.User;
import com.zyq.bloggy.model.vo.UserVo;
import com.zyq.bloggy.serivce.UserService;
import com.zyq.bloggy.util.StringUtils;
import com.zyq.bloggy.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@CacheConfig(cacheNames = "user")
public class UserServiceImpl implements UserService {
    //存储用户角色的键，时间为600秒
    private static final String KEY_USER_ROLE = "user:role#600";
    private static final String DEFAULT_AVATAR = "http://localhost:8080/default/logo.svg";
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserVoMapper userVoMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public UserVo login(String account, String password) {
        account = account.trim();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPassword, DigestUtils.md5Hex(password)).eq(User::getStatus, Status.ACTIVE.getCode());
        if (StringUtils.isEmail(account)) {
            wrapper.eq(User::getEmail, account);
        } else {
            wrapper.eq(User::getUsername, account);
        }
        return userVoMapper.toVO(userMapper.selectOne(wrapper));
    }

    @Override
    public Boolean closeAccount(Long id) {
        return userMapper.update(null, new LambdaUpdateWrapper<User>().eq(User::getId, id).eq(User::getRole, Role.MEMBER.getCode()).set(User::getStatus, Status.INACTIVE.getCode())) > 0;
    }

    @Override
    public User updateProfile(User user) {
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if (!user.getNickname().isEmpty()) {
            lambdaUpdateWrapper.set(User::getNickname, user.getNickname());
        }
        if (!user.getAvatar().isEmpty()) {
            lambdaUpdateWrapper.set(User::getAvatar, user.getAvatar());
        }
        userMapper.update(user, lambdaUpdateWrapper.eq(User::getId, user.getId()));
        return user;
    }

    @Override
    @CachePut(cacheNames = "reg#43200", key = "#user.email")
    public User regBeforeVerify(User user) {
        if (StringUtils.isAnyBlank(user.getUsername(), user.getPassword(), user.getEmail())) {
            throw new BusinessException("请先完善信息");
        }
        if (!StringUtils.isEmail(user.getEmail())) {
            throw new BusinessException("请输入合法的邮箱");
        }
        if (StringUtils.isEmail(user.getUsername())) {
            throw new BusinessException("请勿使用非法字符作为用户名");
        }
        if (checkUsernameIsUsed(user.getUsername()) || checkEmailIsUsed(user.getEmail())) {
            throw new BusinessException("该用户名或邮箱已被使用，请更换");
        }
        user.setUsername(user.getUsername().trim());
        user.setPassword(user.getPassword().trim());
        user.setEmail(user.getEmail().trim());
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        return user;
    }

    @Override
    public List<Map<String, Integer>> getUserCountByDay(Integer num) {
        return userMapper.getCountByTime(TimeUtil.getTimestampRange(num));
    }


    @Override
    @CacheEvict(cacheNames = "reg", key = "#email")
    public Boolean register(String email) {
        User user = (User) redisTemplate.opsForValue().get("reg:" + email);
        if (StringUtils.isAnyBlank(user.getUsername(), user.getPassword(), user.getEmail())) {
            throw new ServiceException(Code.SYSTEM_ERROR.getCode(), "系统缓存出现错误");
        }
        user.setRole(Role.MEMBER.getCode());
        user.setNickname(user.getUsername());
        user.setAvatar(DEFAULT_AVATAR);
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
        );
        List<UserVo> userVos = new ArrayList<>();
        userList.forEach(user -> userVos.add(userVoMapper.toVO(user)));
        return userVos;
    }

    @Override
    public Page<User> getUser(Integer page) {
        page = page < 1 ? 1 : page;
        int pageSize = 15;
        int current = (page - 1) * pageSize;
        Page<User> userPage = new Page<>();
        userPage.setTotal(userMapper.selectCount(null));
        userPage.setRecords(userMapper.getUserList(current, pageSize));
        return userPage;

    }

    @Override
    @Cacheable(cacheNames = KEY_USER_ROLE, key = "#id")
    public Integer getRole(Long id) {
        return userMapper.selectById(id).getRole();
    }


    @Override
    public boolean checkUsernameIsUsed(String username) {
        return Objects.nonNull(userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username)));
    }

    @Override
    public boolean checkEmailIsUsed(String email) {
        return Objects.nonNull(userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email)));
    }

    @Override
    public int getUserCount() {
        return userMapper.getCount();
    }

    @Override
    public int getActiveUserCount() {
        return Math.toIntExact(userMapper.selectCount(null));
    }
}
