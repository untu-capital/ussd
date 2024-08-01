package com.example.ussd1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients
@Slf4j
public class Ussd1Application {

    public static void main(String[] args) {
        SpringApplication.run(Ussd1Application.class, args);
        log.info("<<<<<<<<<<<<<<<APPLICATION STARTED>>>>>>>>>>>>>>>");
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
