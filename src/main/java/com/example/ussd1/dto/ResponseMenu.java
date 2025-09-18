package com.example.ussd1.dto;

import lombok.Data;

@Data
public class ResponseMenu {

    private String stage;
    private StringBuilder message;
    private String transactionType;

    @Override
    public String toString() {
        return message != null ? message.toString() : "";
    }

}
