package io.swagger.controller;

import io.swagger.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/secured/sms")
@RestController
public class SmsController {

    @Autowired
    private SmsService smsService;

    @GetMapping(value = "/send")
    public ResponseEntity sendSms(@RequestParam("phone") String phone,
                                  @RequestParam("message") String message) {

        return ResponseEntity.ok( smsService.sendSms(phone, message) );

    }

}
