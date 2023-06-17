package com.zyq.bloggy.config;

import com.zyq.bloggy.task.ArticleTask;
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
    public Trigger scheduleJobDetailTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours(2)
                .repeatForever();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .forJob(ArticleJobDetail())
                .withIdentity("articleJob")
                .withSchedule(scheduleBuilder)
                .build();
        System.out.println("articleJob trigger end");
        return trigger;
    }


}
