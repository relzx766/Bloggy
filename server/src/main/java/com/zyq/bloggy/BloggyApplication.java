package com.zyq.bloggy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.zyq.bloggy.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
@EnableCaching
@EnableAsync
public class BloggyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BloggyApplication.class, args);
    }
}
