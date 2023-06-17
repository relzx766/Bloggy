package com.zyq.bloggy.serivce.impl;

import com.zyq.bloggy.exception.BusinessException;
import com.zyq.bloggy.exception.ServiceException;
import com.zyq.bloggy.pojo.Article;
import com.zyq.bloggy.pojo.User;
import com.zyq.bloggy.serivce.ArticleService;
import com.zyq.bloggy.serivce.MailService;
import com.zyq.bloggy.serivce.UserService;
import com.zyq.bloggy.util.ValidateCodeUtil;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@CacheConfig(cacheNames = "mail")
public class MailServiceImpl implements MailService {
    @Autowired
    UserService userService;
    @Autowired
    ArticleService articleService;
    @Resource
    JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String SENDER_MAIL;
    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    RedisTemplate redisTemplate;
    private static final String REG_SUBJECT = "Bloggy:验证您的邮箱";
    private static final String ADVICE_SUBJECT = "Bloggy:通知";

    @Override
    @Async
    @CachePut(cacheNames = "validateCode#1200", key = "#email")
    public String sendValidateCode(String email) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(SENDER_MAIL);
            helper.setTo(email);
            helper.setSubject(REG_SUBJECT);
            Context context = new Context();
            String code = ValidateCodeUtil.createMailCode();
            context.setVariable("code", code);
            String content = templateEngine.process("validateMail", context);
            helper.setText(content, true);
            javaMailSender.send(mimeMessage);
            return code;
        } catch (MessagingException e) {
            throw new BusinessException("邮件发送错误", e);
        }
    }

    @Override
    //@CacheEvict(cacheNames = "validateCode",key = "#email")
    public Boolean verify(String email, String code) throws NullPointerException {
        code = code.toUpperCase();
        String resultVal = (String) redisTemplate.opsForValue().get("validateCode:" + email);
        if (code.equals(resultVal)) {
            return true;
        }
        return false;
    }

    @Override
    @Async
    public Boolean sendWorkDeletedAdvice(Long id, String reason) {
        Map<String, Object> map = new HashMap<>();
        Article article = articleService.getById(id);
        User user = userService.getUserDetail(article.getAuthor());
        map.put("account", user.getUsername());
        map.put("id", id);
        map.put("title", article.getTitle());
        map.put("reason", reason);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(SENDER_MAIL);
            helper.setTo(user.getEmail());
            helper.setSubject(ADVICE_SUBJECT);
            Context context = new Context();
            context.setVariables(map);
            String content = templateEngine.process("authorAdviceMail", context);
            helper.setText(content, true);
            javaMailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            throw new ServiceException("邮件发送失败", e);
        }
    }
}
