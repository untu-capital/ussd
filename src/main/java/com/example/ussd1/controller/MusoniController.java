package com.example.ussd1.controller;

import com.example.ussd1.service.MusoniService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "musoni", produces = "application/json")
public class MusoniController {
    private final MusoniService musoniService;

    // TODO: 1/2/2023 Create Client
    // TODO: 31/1/2023 Get Client by phone number
    // TODO: 1/2/2023 error handling when the network is down

    @GetMapping("clients")
    public String getAllClients(){

        return musoniService.getAllClients();
    }

    @GetMapping("clients/{clientId}")
    public String getClientById(@PathVariable String clientId){

        return musoniService.getClientById(clientId);
    }

    @GetMapping("clients/get-client-by-mobileNo/{mobileNo}/{secondaryMobileNo}")
    public String getClientByMobileNo(@PathVariable("mobileNo") String mobileNo, @PathVariable("secondaryMobileNo")String secondaryMobileNo){
        return musoniService.getClientByMobileNo(mobileNo, secondaryMobileNo);
    }

    @GetMapping("clients/query")
    public String getClientByQueryString(@RequestParam("search") String queryString){
        return musoniService.getClientByQueryString(queryString);
    }

    @GetMapping("loans")
    public String getAllLoans(){
        return musoniService.getAllLoans();
    }

    @GetMapping("loans/{loanAccount}")
    public String getLoanByLoanAccount(@PathVariable String loanAccount){
        return musoniService.getLoanByLoanAccount(loanAccount);
    }
    @GetMapping("loans/query")
    public String getLoanByQueryString(@RequestParam("search") String queryString){
        return musoniService.getLoanByQueryString(queryString);
    }

    @GetMapping("loans/mini-statement/{loanAccount}")
    public String getMiniStatement(@PathVariable String loanAccount){

        return musoniService.getMiniStatement(loanAccount);
    }

    @GetMapping("loans/repayment-schedule/{loanAccount}")
    public String getRepaymentSchedule(@PathVariable String loanAccount){

        return musoniService.getRepaymentSchedule(loanAccount);
    }

    @GetMapping("loans/associations/{loanAccount}")
    public String getAllAssociations(@PathVariable String loanAccount){
        return musoniService.getAllAssociations(loanAccount);
    }
}
