package com.example.ussd1.DTO;

import lombok.Data;

@Data
public class MiniStatementDTO {
    private Object principalDisbursed;
    private Object currency;
    private Object totalRepayment;
    private Object totalExpectedRepayment;
    private Object totalOutstanding;

}
