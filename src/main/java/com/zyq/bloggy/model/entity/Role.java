package com.zyq.bloggy.model.entity;

public enum Role {
    //管理员
    ADMIN(0),
    //普通用户
    MEMBER(1),
    //游客
    VISITOR(2);
    private Integer code;

    public Integer getCode() {
        return code;
    }

    public static String getRole(int code) {
        return switch (code) {
            case 0 -> ADMIN.toString();
            case 1 -> MEMBER.toString();
            default -> VISITOR.toString();
        };
    }

    Role(Integer code) {
        this.code = code;
    }

}
