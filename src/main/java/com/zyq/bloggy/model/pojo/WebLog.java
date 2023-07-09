package com.zyq.bloggy.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_log")
public class WebLog {
    private Long userId;
    private String type;
    private Timestamp startTime;
    private Integer spendTime;
    private String url;
    private String ip;
    private String method;
    private String parameter;
    private String result;
}
