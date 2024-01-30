package com.example.ussd1.controller;

import com.example.ussd1.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "sms", produces = "application/json")
public class SmsController {

    private final SmsService smsService;

    @GetMapping("single/{destination}/{messageText}")
    public String sendSingle(@PathVariable String destination, @PathVariable String messageText){
        return smsService.sendSingle(destination, messageText);
    }

    @GetMapping("bulk")
    public String sendBulk(){
        return smsService.sendBulk();
    }

    @GetMapping("balance")
    public String getBalance(){
        return smsService.getBalance();
    }

    @GetMapping("time")
    public String getTime(){
        return smsService.SchedulerConfig();
    }
}
