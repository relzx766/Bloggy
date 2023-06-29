package com.zyq.bloggy.serivce;

public interface MailService {
    void sendValidateCode(String email);

    Boolean verify(String email, String code);

    void sendWorkDeletedAdvice(Long id, String reason);
}
