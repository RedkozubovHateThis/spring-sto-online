package io.swagger.service;

import io.swagger.response.api.APIResponse;

/**
 * Created by phoenix on 31.10.17.
 */
public interface SmsService {
    APIResponse sendSms(String phone, String message);
    APIResponse sendSms(String phone, String message, Integer translit, String charset);
    void sendSmsAsync(String phone, String message);
    void sendSmsAsync(String phone, String message, Integer translit, String charset);
}
