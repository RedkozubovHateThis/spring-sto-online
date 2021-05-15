package io.swagger.service;

public interface MailSendService {
    void sendPasswordRestoreMail(String email, String subject, String uuid);
}
