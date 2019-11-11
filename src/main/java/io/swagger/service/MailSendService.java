package io.swagger.service;

import org.springframework.scheduling.annotation.Async;

public interface MailSendService {
    void sendPasswordRestoreMail(String email, String subject, String uuid);
}
