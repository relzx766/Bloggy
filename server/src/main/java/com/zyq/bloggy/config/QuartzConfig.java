package com.zyq.bloggy.config;

import com.zyq.bloggy.task.ArticleTask;
import com.zyq.bloggy.task.CommentTask;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail ArticleJobDetail() {
        return JobBuilder.newJob(ArticleTask.class)
                .withIdentity("articleJob")
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail CommentJobDetail() {
        return JobBuilder.newJob(CommentTask.class)
                .withIdentity("commentJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger ArticleJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .forJob(ArticleJobDetail())
                .withIdentity("articleJob")
                .withSchedule(scheduleBuilder)
                .build();
        return trigger;
    }

    @Bean
    public Trigger CommentJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(2)
                .repeatForever();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .forJob(CommentJobDetail())
                .withIdentity("commentJob")
                .withSchedule(scheduleBuilder)
                .build();
        return trigger;
    }


}
