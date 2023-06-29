package com.zyq.bloggy.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_tag")
public class Tag {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String content;
    @TableLogic
    private Integer status;
    private Timestamp createTime;
}
