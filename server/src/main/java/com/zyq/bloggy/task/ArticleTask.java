package com.zyq.bloggy.task;

import com.zyq.bloggy.serivce.ArticleService;
import com.zyq.bloggy.util.StringUtils;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("articleTask start--------{}", dateFormat.format(System.currentTimeMillis()));
        try {
            articleService.saveLikeToDB();
            articleService.updateDBView();
            articleService.updateTrend();
            articleService.updateCommentNum();
            articleService.decrementTrend();
        } catch (Exception e) {
            log.warn("articleTask 运行时出现异常-------{}", StringUtils.getExceptionInfo(e));

        }

        log.info("articleTask end--------{}", dateFormat.format(System.currentTimeMillis()));
    }
}
