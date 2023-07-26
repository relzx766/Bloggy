package com.zyq.bloggy.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.zyq.bloggy.model.entity.Result;
import com.zyq.bloggy.model.pojo.User;
import com.zyq.bloggy.model.vo.UserVo;
import com.zyq.bloggy.serivce.MailService;
import com.zyq.bloggy.serivce.UserService;
import com.zyq.bloggy.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    MailService mailService;


    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        userService.regBeforeVerify(user);
        mailService.sendValidateCode(user.getEmail());
        return Result.ok();
    }

    @PostMapping("/verify")
    public Result verify(@RequestParam("email") String email,
                         @RequestParam("code") String code) {
        if (mailService.verify(email, code)) {
            if (userService.register(email)) {
                return Result.ok("验证成功");
            }
        }
        return Result.err("验证失败");
    }

    @PostMapping("/login")
    public Result login(@RequestParam("account") String account,
                        @RequestParam("password") String password) {
        UserVo userVo = userService.login(account, password);
        if (Objects.nonNull(userVo)) {
            StpUtil.login(userVo.getId());
            Map<String, Object> data = new HashMap<>();
            data.put("user", userVo);
            data.put("token", StpUtil.getTokenInfo());
            return Result.ok(data, "登录成功");
        }
        return Result.err("登录失败");
    }

    @GetMapping("/logOut")
    @SaCheckLogin
    public void logOut() {
        StpUtil.logout();
    }

    //注销
    @GetMapping("/unsubscribe")
    @SaCheckLogin
    public Result unsubscribe() {
        Long id = StpUtil.getLoginIdAsLong();
        return userService.closeAccount(id) ? Result.ok("注销账户成功") : Result.err("注销账户失败");
    }

    @GetMapping("/profile/{id}")
    public Result getProfile(@PathVariable("id") Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("user", userService.getUser(id));
        return Result.ok(data);
    }


    @PostMapping("/profile/update")
    public Result updateProfile(@RequestParam("nickname") String nickname,
                                @RequestParam("avatar") MultipartFile multipartFile) {
        Map<String, Object> data = new HashMap<>();
        User user = new User();
        user.setId(StpUtil.getLoginIdAsLong());
        user.setNickname(nickname);
        if (!multipartFile.isEmpty()) {
            user.setAvatar(FileUtil.save(multipartFile));
        }
        data.put("user", userService.updateProfile(user));
        return Result.ok(data);
    }

    @GetMapping("/search/{keyword}")
    public Result searchUser(@PathVariable("keyword") String keyword) {
        Map<String, Object> data = new HashMap<>();
        data.put("users", userService.getUser(keyword));
        return Result.ok(data);
    }

    @GetMapping("/list/{page}")
    @SaCheckRole("ADMIN")
    public Result getUsers(@PathVariable("page") Integer page) {
        Map<String, Object> data = new HashMap<>();
        data.put("page", userService.getUser(page));
        return Result.ok(data);
    }

    @PostMapping("/close")
    @SaCheckRole("ADMIN")
    public Result closeAccount(@RequestParam("id") Long id) {
        userService.closeAccount(id);
        log.info(String.format("管理员{%s}对账号{%s}执行了注销操作", StpUtil.getLoginIdAsString(), id));
        return Result.ok();
    }

    @PostMapping("/active")
    public Result activeAccount(@RequestParam("id") Long id) {
        userService.activeAccount(id);
        log.info(String.format("用户{%s}账号重新激活", id));
        return Result.ok();
    }

    @GetMapping("/count")
    public Result getCount() {
        Map<String, Object> data = new HashMap<>();
        data.put("all", userService.getUserCount());
        data.put("active", userService.getActiveUserCount());
        return Result.ok(data);
    }

    @GetMapping("/count/{num}")
    public Result getCountByDay(@PathVariable("num") Integer num) {
        return new Result().success().put("records", userService.getUserCountByDay(num));
    }

}
