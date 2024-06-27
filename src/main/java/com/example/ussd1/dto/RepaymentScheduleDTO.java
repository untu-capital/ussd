package com.example.ussd1.dto;

import lombok.Data;

@Data
public class RepaymentScheduleDTO {
    private Object loanTermInDays;
    private Object dueDate;
    private Object totalPaid;
    private Object totalDue;
    private Object totalOutstanding;

}
