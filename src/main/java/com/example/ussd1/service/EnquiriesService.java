package com.example.ussd1.service;

import com.example.ussd1.DTO.MiniStatementDTO;
import com.example.ussd1.DTO.RepaymentScheduleDTO;
import com.example.ussd1.entity.CollateralSecurity;
import com.example.ussd1.entity.LoanApplication;
import com.example.ussd1.repository.CollateralSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class EnquiriesService {


    @Autowired
    private final RestTemplate restTemplate;

    @Value("${musoni.url}")
    private String musoniBaseURl;
    @Value("${musoni.username}")
    private String username;
    @Value("${musoni.password}")
    private String password;
    @Value("${musoni.X_FINERACT_PLATFORM_TENANTID}")
    private String X_FINERACT_PLATFORM_TENANTID;
    @Value("${musoni.X_API_KEY}")
    private String X_API_KEY;

    @Autowired
    private final MusoniService musoniService;

    @Autowired
    private final CollateralSecurityRepository collateralSecurityRepository;


    public String getMiniStatement(String loanAccount){

        Locale usa = new Locale("en", "US");
        NumberFormat currency = NumberFormat.getCurrencyInstance(usa);

        JSONObject jsonLoan  = setMusoniHeaders(loanAccount);
        System.out.println(jsonLoan);
        MiniStatementDTO miniStatementDTO = new MiniStatementDTO();
        JSONObject jsonLoanSummary = jsonLoan.getJSONObject("summary");
        miniStatementDTO.setPrincipalDisbursed(currencyFormatter(jsonLoanSummary.getBigDecimal("principalDisbursed")));
        miniStatementDTO.setTotalExpectedRepayment(currencyFormatter(jsonLoanSummary.getBigDecimal("totalExpectedRepayment")));
        miniStatementDTO.setTotalOutstanding(currencyFormatter(jsonLoanSummary.getBigDecimal("totalOutstanding")));
        miniStatementDTO.setTotalRepayment(currencyFormatter(jsonLoanSummary.getBigDecimal("totalRepayment")));
        miniStatementDTO.setCurrency(jsonLoanSummary.getJSONObject("currency").get("code"));

        System.out.println(miniStatementDTO);

        return "<b>Loan Balance for Account: "+loanAccount+"</b>"+
//                "\nCurrency: "+miniStatementDTO.getCurrency()+
                "\nLoan Amount: "+miniStatementDTO.getPrincipalDisbursed()+
                "\nAmount Paid: "+miniStatementDTO.getTotalRepayment()+
                "\nTotal Expected Payment: "+miniStatementDTO.getTotalExpectedRepayment()+
                "\nRemaining Balancce: "+miniStatementDTO.getTotalOutstanding();
    }

//       ToDo: Get Required information from the loan returned
    public String repaymentSchedule(String loanAccount) throws ParseException {

        Locale usa = new Locale("en", "US");
        NumberFormat currency = NumberFormat.getCurrencyInstance(usa);

        String jsonLoan  = musoniService.getRepaymentSchedule(loanAccount);
//        System.out.println(jsonLoan);
        RepaymentScheduleDTO repaymentScheduleDTO = new RepaymentScheduleDTO();

        String repaymentSchedulPeriods = new String(String.valueOf(new JSONObject(jsonLoan).getJSONObject("repaymentSchedule").getJSONArray("periods").length()));

        System.out.println("####################  START  #############################");
        System.out.println(repaymentSchedulPeriods);
        System.out.println("#################### END   #############################");

//        Iterator<String> keys = repaymentSchedulPeriods.keys();
//
//        while (keys.hasNext()) {
//            String key = keys.next();
//            int x = 1;
//            if (repaymentSchedulPeriods.get(key) instanceof JSONObject) {
//                System.out.println(" done here");
//                x++;
//
//            }
//        }
//        int
//        for (period : repaymentSchedulPeriods) {
//
//        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy,MM,dd");
        Date date = new Date();
//        Date date = new Date(119,9,03);
        String current_date = df.format(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy,MM,dd", Locale.ENGLISH);

        LocalDate currentDate = LocalDate.parse(current_date, formatter);

        JSONObject repaymentSchedul = null;
        for (int period = 1; period<Integer.parseInt(repaymentSchedulPeriods); period++ ) {
            repaymentSchedul = new JSONObject(new JSONObject(jsonLoan).getJSONObject("repaymentSchedule").getJSONArray("periods").get(period).toString());

            String due_dates = repaymentSchedul.getJSONArray("dueDate").get(0).toString() + "," + repaymentSchedul.getJSONArray("dueDate").get(1).toString() + "," + repaymentSchedul.getJSONArray("dueDate").get(2).toString();
            DateFormat inputFormat = new SimpleDateFormat("yyyy,MM,dd", Locale.US);
            Date dates = inputFormat.parse(due_dates);
            String due_date = df.format(dates);
            LocalDate dueDate = LocalDate.parse(due_date, formatter);

            repaymentScheduleDTO.setDueDate(dueDate);
            repaymentScheduleDTO.setTotalOutstanding(currencyFormatter(repaymentSchedul.getBigDecimal("totalOutstandingForPeriod")));
            repaymentScheduleDTO.setTotalDue(currencyFormatter(repaymentSchedul.getBigDecimal("totalDueForPeriod")));
            repaymentScheduleDTO.setTotalPaid(currencyFormatter(repaymentSchedul.getBigDecimal("totalPaidForPeriod")));

            if (dueDate.compareTo(currentDate) > 0){
                System.out.println("Next Repayment date is: "+ dueDate);
                break;
            }
        }

        System.out.println(repaymentScheduleDTO);
        return "<b>Loan Balance for Account: "+loanAccount+" for period </b>"+
                "\nDue Date: "+repaymentScheduleDTO.getDueDate()+
                "\nAmount Due: "+repaymentScheduleDTO.getTotalDue()+
                "\nAmount paid: "+repaymentScheduleDTO.getTotalPaid()+
                "\nAmount Outstanding: "+repaymentScheduleDTO.getTotalOutstanding()+
//                "\nprint data: "+repaymentSchedulPeriods+
                "\nCurrent date: "+currentDate;
//                "\nDue date: "+ LocalDate.of(2023, 02, 10);
    }

    public String showCollateralSecurity(String loanAccount){
        return "Request Collateral Securities for Loan Account: "+loanAccount+ " ?" +
                "\n1. Confirm"+
                "\n2. Cancel";
    }

    public String sendCollateralSecurityRequest(String loanAccount, String phoneNumber){
        String jsonLoan  = musoniService.getLoanByLoanAccount(loanAccount);
        String clientName = new String(new JSONObject(jsonLoan).get("clientName").toString());
        saveLoanApplication(phoneNumber, loanAccount, clientName);
        return "We've received your request.. We will contact you soon.";
    }

    public void saveLoanApplication(String phoneNumber, String loanAccount, String clientName){
        CollateralSecurity collateralSecurity = new CollateralSecurity();
        collateralSecurity.setPhoneNumber(phoneNumber);
        collateralSecurity.setLoanAcc(loanAccount);
        collateralSecurity.setClientName(clientName);
        collateralSecurity.setCollateralDescription(clientName + " has requested Collateral Securities for Loan Account: " + loanAccount + "\n\nKindly attend");
        System.out.println(collateralSecurity);
        collateralSecurityRepository.save(collateralSecurity);
    }

    public String showAllLoanAccounts(String phoneNumber){
        return "SHOW LOAN ACCOUNTS - MUSONI FOR ~ "+phoneNumber;
    }

    public JSONObject setMusoniHeaders(String loanId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(username, password);
        headers.set("X-Fineract-Platform-TenantId", X_FINERACT_PLATFORM_TENANTID);
        headers.set("x-api-key", X_API_KEY);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String loan = restTemplate.exchange(musoniBaseURl+"loans/"+ loanId, HttpMethod.GET, entity, String.class).getBody();
        System.out.println(loan);
        return new JSONObject(loan);
    }

    public String currencyFormatter(BigDecimal amount){
        Locale usa = new Locale("en", "US");
        NumberFormat currency = NumberFormat.getCurrencyInstance(usa);
        return currency.format(amount);
    }
}
