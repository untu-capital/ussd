package com.example.ussd1.service;

import com.example.ussd1.DTO.SMSDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@EnableScheduling
public class SmsService {

    private final RestTemplate restTemplate ;

    @Value("${esolutions.url}")
    private String eSolutionsBaseURL;
    @Value("${esolutions.username}")
    private String username;
    @Value("${esolutions.password}")
    private String password;

    public SmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public HttpHeaders setESolutionsHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(username, password);
        headers.set("Content-Type", "application/json");

        return headers;
    }

    public String sendSingle(String destination, String messageText) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String messageDate = dateFormat.format(new Date()) + 5;
        String messageReference = UUID.randomUUID().toString();
        SMSDto smsDto = new SMSDto("UNTU",destination,messageText,messageReference,messageDate,"","");
        HttpEntity<SMSDto> entity = new HttpEntity<>(smsDto, setESolutionsHeaders());
        return restTemplate.exchange(eSolutionsBaseURL + "single", HttpMethod.POST, entity, String.class).getBody();
    }
    public String sendBulk() {
        String batchNumber = "B" + UUID.randomUUID();
        String s = """
                {
                "batchNumber": "%s",
                "messages": [
                {
                "originator":"UNTU",
                "destination":"263784315526",
                "messageText":"This is Spring test message",
                "messageReference":"R99578J"
                },
                {
                "originator":"UNTU",
                "destination":"263775797299",
                "messageText":"This is Spring test message",
                "messageReference":"R99577J"
                },
                {
                "originator":"UNTU",
                "destination":"263783229685",
                "messageText":"This is Spring test message",
                "messageReference":"R99576J"
                }
                ]
                }
                """.formatted(batchNumber);
        HttpEntity<String> entity = new HttpEntity<>(s, setESolutionsHeaders());
        return restTemplate.exchange(eSolutionsBaseURL + "bulk", HttpMethod.POST, entity, String.class).getBody();
    }

    public String getBalance() {
        HttpEntity<String> entity = new HttpEntity<>( setESolutionsHeaders());
        return restTemplate.exchange(eSolutionsBaseURL + "balance/UNTUAPI", HttpMethod.GET, entity, String.class).getBody();
    }

    @Scheduled(fixedRate = 5000)
    public String SchedulerConfig() {
        System.out.println("The time is now {}");
        return "The time is now {}";
    }
}
