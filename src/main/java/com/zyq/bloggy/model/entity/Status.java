package com.zyq.bloggy.model.entity;

public enum Status {
    //活跃状态,点赞状态
    ACTIVE(1),
    //删除，注销，取消点赞状态
    INACTIVE(0),
    //注册但未激活，邮箱未验证
    NONACTIVE(2);
    private Integer code;

    Status(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
