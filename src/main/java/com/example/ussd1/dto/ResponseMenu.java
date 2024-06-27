package com.example.ussd1.dto;

import com.example.ussd1.dto.res.MessageResponse;
import lombok.Data;

@Data
public class ResponseMenu {

    private String stage;
    private StringBuilder message;
    private String transactionType;

}
