package com.zyq.bloggy.serivce;

import com.zyq.bloggy.model.pojo.WebLog;

public interface LogService {
    void addLogToQueue(WebLog webLog);

    void saveLogTask();

    void delInfoRecordTask();
}
