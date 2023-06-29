package com.zyq.bloggy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {
    private Integer id;
    private String username;
    private String nickname;
    private String avatar;
    private String role;
    private Integer status;
}
