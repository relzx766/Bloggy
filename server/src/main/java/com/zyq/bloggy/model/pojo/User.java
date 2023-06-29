package com.zyq.bloggy.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String username;
    @TableField(value = "passwd", select = false)
    private String password;
    private String nickname;
    private String avatar;
    @Email
    private String email;
    private Integer role;
    @TableLogic
    private Integer status;
    private Timestamp registrationTime;
    private Timestamp lastLoginTime;
}
