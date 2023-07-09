package com.zyq.bloggy.serivce.impl;

import com.zyq.bloggy.annotation.TaskInfo;
import com.zyq.bloggy.mapper.LogMapper;
import com.zyq.bloggy.model.pojo.WebLog;
import com.zyq.bloggy.serivce.LogService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
@Slf4j
public class LogServiceImpl implements LogService {
    @Autowired
    LogMapper logMapper;
    static int DELETE_OFFSET = 20;
    private final Executor executor = Executors.newSingleThreadExecutor();
    public final ConcurrentLinkedQueue<WebLog> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void addLogToQueue(WebLog webLog) {
        queue.add(webLog);
    }

    @Override
    @PostConstruct
    public void saveLogTask() {
        CompletableFuture.runAsync(() -> {
            while (true) {
                WebLog webLog = queue.poll();
                if (null != webLog) {
                    logMapper.insert(webLog);
                }
            }
        }, executor);
    }

    @Override
    @Async
    @Scheduled(cron = "0 */4 * * * *")
    @TaskInfo(group = "log", value = "delete info", description = "删除类型为info的日志")
    public void delInfoRecordTask() {
        logMapper.delInfoTypeRecordLimit(DELETE_OFFSET);
    }


}
