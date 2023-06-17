package com.zyq.bloggy.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.zyq.bloggy.pojo.Result;
import com.zyq.bloggy.pojo.User;
import com.zyq.bloggy.serivce.MailService;
import com.zyq.bloggy.serivce.UserService;
import com.zyq.bloggy.util.FileUtil;
import com.zyq.bloggy.vo.UserVo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    MailService mailService;
    private Logger logger = Logger.getLogger(this.getClass());

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

    @GetMapping("/state/{id}")
    public Result getState(@PathVariable("id") Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("state", userService.getUserStats(id));
        return Result.ok(data);
    }

    @PostMapping("/profile/update")
    public Result updateProfile(@RequestParam("nickname") String nickname,
                                @RequestParam("avatar") MultipartFile multipartFile) {
        Map<String, Object> data = new HashMap<>();
        User user = new User();
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
        logger.info(String.format("管理员{%s}对账号{%s}执行了注销操作", StpUtil.getLoginIdAsString(), id));
        return userService.closeAccount(id) ? Result.ok() : Result.err();
    }
}
