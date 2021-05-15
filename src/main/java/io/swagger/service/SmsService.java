package io.swagger.service;

import io.swagger.response.api.SMSAPIResponse;

/**
 * Created by phoenix on 31.10.17.
 */
public interface SmsService {
    SMSAPIResponse sendSms(String phone, String message);
    SMSAPIResponse sendSms(String phone, String message, Integer translit, String charset);
    void sendSmsAsync(String phone, String message);
    void sendSmsAsync(String phone, String message, Integer translit, String charset);
}
