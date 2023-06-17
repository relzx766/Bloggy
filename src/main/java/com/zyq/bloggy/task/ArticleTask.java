package com.zyq.bloggy.task;

import com.zyq.bloggy.serivce.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
@Slf4j
public class ArticleTask extends QuartzJobBean {
    @Autowired
    ArticleService articleService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
        log.info("articleTask start--------{}", date);
        articleService.saveLikeToDB();
        articleService.updateDBView();
        articleService.getTrend();
        articleService.updateCommentNum();
    }
}
