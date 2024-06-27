package com.example.ussd1.dto;

import lombok.Data;

@Data
public class MiniStatementDTO {
    private Object principalDisbursed;
    private Object currency;
    private Object totalRepayment;
    private Object totalExpectedRepayment;
    private Object totalOutstanding;

}
