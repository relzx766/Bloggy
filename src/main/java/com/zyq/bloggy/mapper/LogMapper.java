package com.zyq.bloggy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyq.bloggy.model.pojo.WebLog;
import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Repository;

@Repository
public interface LogMapper extends BaseMapper<WebLog> {
    @Delete("delete from tb_log where type='info' limit #{offset}")
    int delInfoTypeRecordLimit(int offset);
}
