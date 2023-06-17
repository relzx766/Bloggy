package com.zyq.bloggy.serivce;

public interface MailService {
    String sendValidateCode(String email);

    Boolean verify(String email, String code);

    Boolean sendWorkDeletedAdvice(Long id, String reason);
}
