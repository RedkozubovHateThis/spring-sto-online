package io.swagger.service;

import org.springframework.scheduling.annotation.Async;

public interface MailSendService {
    void sendMail(String email, String subject, String mailText);
}
