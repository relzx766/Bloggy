package com.zyq.bloggy.task;

import com.zyq.bloggy.serivce.ArticleCommentService;
import com.zyq.bloggy.serivce.ReplyCommentService;
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
public class CommentTask extends QuartzJobBean {
    @Autowired
    ArticleCommentService articleCommentService;
    @Autowired
    ReplyCommentService replyCommentService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("CommentTask start--------{}", dateFormat.format(System.currentTimeMillis()));
        try {
            articleCommentService.saveLikeToDB();
            replyCommentService.saveLikeToDB();
        } catch (Exception e) {
            log.warn("CommentTaskTask 运行时出现异常-------{}", StringUtils.getExceptionInfo(e));
        }

        log.info("CommentTask end--------{}", dateFormat.format(System.currentTimeMillis()));
    }
}
