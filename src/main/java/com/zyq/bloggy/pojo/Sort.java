package com.zyq.bloggy.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_sort")
public class Sort {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long owner;
    private String title;
    private ArrayList<Article> articles;
    private String cover;
    @TableLogic
    private Integer status;
    private Timestamp createTime;
}
