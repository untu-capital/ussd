package com.example.ussd1.client;

import com.example.ussd1.dto.Loan;
import com.example.ussd1.dto.req.LoanRequest;
import com.example.ussd1.entity.LoanApplication;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "utgClient", url = "http://13.246.85.3:7878/api/utg/")
public interface UtgClient {

    @GetMapping("credit_application/userByPhone")
    Loan getLoanApplication(@RequestParam String phoneNumber);
    @PostMapping("credit_application")
    void saveLoanApplication(@RequestBody LoanRequest loanApplication);


}
