package com.example.ussd1.service;

import com.example.ussd1.commons.UssdConstants;
import com.example.ussd1.dto.ResponseMenu;
import com.example.ussd1.dto.res.MessageResponse;
import com.example.ussd1.entity.LoanApplication;
import com.example.ussd1.entity.UserEntity;
import com.example.ussd1.repository.LoanApplicationRepository;
import com.example.ussd1.repository.UserRepository;
import com.example.ussd1.util.StringUtil;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONObject;

@Service
@AllArgsConstructor
@Slf4j
public class ApplyForLoanService {
    @Autowired
    private final LoanApplicationRepository loanApplicationRepository;
    @Autowired
    private final ContactInfo contactInfo;
    @Autowired
    private final IndustryService industryService;
    @Autowired
    private final EnquiriesService enquiriesService;
    @Autowired
    private final MusoniService musoniService;
    @Autowired
    private final UserRepository userRepository;

    private final UserService userService;
    private final SmsService smsService;

    // Create a logger instance
    private static final Logger logger = Logger.getLogger(ApplyForLoanService.class.getName());

    public ResponseMenu applyForLoanMenu(String text, String phoneNumber, String[] level, String transactionID) throws ParseException {


        MessageResponse messageResponse = new MessageResponse();
        ResponseMenu responseMenu = new ResponseMenu();
        String branches = contactInfo.getAllBranches();
        String industries = industryService.getAllIndustries();
        Optional<UserEntity> user = userRepository.findByPhoneNumber(phoneNumber);
        StringBuilder menu = new StringBuilder();

        String pin = user.get().getPin();
        int startIndex = Arrays.asList(level).indexOf(pin) + 1;
        String[] levels = Arrays.copyOfRange(level, startIndex, level.length);


//        String[] levels = text.substring(text.indexOf(user.get().getPin())).split("\\*");

        log.info("LEVELS LENGTH-LOANS:{}",levels.length);
        log.info("LEVELS STRING-LOANS:{}",levels);
        log.info("TEXT : {}", text);

        if(levels.length == 0){
            menu.append("What would you like to do ?\n1. Apply For A Loan\n2. View next instalment\n3. Request for Account No\n4. Change Your Pin ");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }
//                Apply for loan
        else if (levels.length == 1 && Objects.equals(levels[0],"1")) {
            menu.append("How much do you need ? (USD)");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }
        else if (levels.length == 2 && Objects.equals(levels[0],"1") && !Objects.equals(levels[1],"") && Integer.parseInt(levels[1]) < 1000 ) {
            menu.append(" Minimum loan amount must be at least $1,000");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_APPLY_LOAN);
        }

//            Start Apply For Repeat Loan
        else if (levels.length == 2 && Objects.equals(levels[0],"1") && !Objects.equals(levels[1],"") && Integer.parseInt(levels[1]) >= 1000) {
            menu.append("Enter loan tenure( in months).\n(Enter the number of months e.g., 3, 6, 9, 12)");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }
//        else if (levels.length == 3 && Objects.equals(levels[0], "1") && !Objects.equals(levels[1],"") && !Objects.equals(levels[2],"")) {
//            menu.append("What industry sector matches your business?").append(industries);
//        } else if (levels.length == 4 && Objects.equals(levels[0], "1") && !Objects.equals(levels[1],"")&& !Objects.equals(levels[2],"")&& !Objects.equals(levels[3],"")) {
//            menu.append("Where are you currently located ?.");
//        }
        else if (levels.length == 3 && Objects.equals(levels[0], "1") && !Objects.equals(levels[1],"") && !Objects.equals(levels[2],"")) {//            String[] tenure = {"1-3 Months","4-6 Months","7-8 Months","9-12 Months"};
//            String branch = contactInfo.getBranchName(levels[5]);
//            String industry = industryService.findIndustryById(levels[4]);
            menu.append("Confirm Loan Application Details: \nLoan Amount : $").append(levels[1]).append("\nTenure: ").append(levels[2]).append("\n\n1. Confirm details\n2. Cancel application");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }
        else if (levels.length == 4 && Objects.equals(levels[0], "1") && !Objects.equals(levels[1],"")&& !Objects.equals(levels[2],"")&& !Objects.equals(levels[3],"")) {            //Save New Loan Application
            saveLoanApplication(levels[1], levels[2], phoneNumber);
            menu.append("Thank you for submitting your loan application to us. We have successfully received it and are currently processing your request. We will keep you updated on the status of your application. Please feel free to contact us if you have any questions or concerns.");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_APPLY_LOAN);

        }else if (levels.length == 6 && !Objects.equals(levels[1],"")&& !Objects.equals(levels[2],"")&& !Objects.equals(levels[3],"")&& !Objects.equals(levels[4],"")&& Objects.equals(levels[5],"2")) {
            menu.append("You've cancelled your application process!");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_APPLY_LOAN);
        }

//                Enter loan account for 1checking balance


        else if (levels.length == 1 && Objects.equals(levels[0], "2")) {
            List<String> loanStatement = musoniService.getClientLoans(user.get().getMusoniClientId());

            log.info("loans list: {}", loanStatement);

            menu.append("Select Loan Account \n").append(musoniService.getClientAccountsList());
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }

        else if ((levels.length == 2 && Objects.equals(levels[0], "2") && !Objects.equals(levels[1], ""))) {
            int loanIndex = Integer.parseInt(levels[1]);
            Map<Integer, String > loanList = StringUtil.processString(musoniService.getClientAccountsList());
            log.info("CLIENT LOAN MAP:{}",loanList);
            String link = "http://localhost:7878/api/utg/musoni/getNextInstalment/" + loanList.get(loanIndex);

            try {
                // Make the HTTP GET request to the API
                HttpResponse<String> response = Unirest.get(link).asString();

                if (response.getStatus() == 200) {
                    // Parse the JSON response
                    JSONObject responseBody = new JSONObject(response.getBody());
                    String dueDate = responseBody.getString("dueDate");
                    double amountDue = responseBody.getDouble("amountDue");

                    // Format the details into a string
                    String instalmentDetails = "Your next instalment details:\n" +
                            "Instalment Date: " + dueDate + "\n" +
                            "Amount Due: " + amountDue;

                    // Append the formatted details to the menu
                    menu.append(instalmentDetails);
                    responseMenu.setMessage(menu);
                    responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
                    responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_NEXT_INSTALMENT_ENQUIRY);
                } else {
                    // Handle the error response
                    menu.append("Unable to fetch instalment details at the moment. Please try again later.");
                    responseMenu.setMessage(menu);
                    responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
                    responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_NEXT_INSTALMENT_ENQUIRY);
                }
            } catch (Exception e) {
                // Log the exception
                logger.log(Level.SEVERE, "Exception occurred while fetching instalment details", e);

                // Handle exceptions
                menu.append("An error occurred. Please try again later.");
                responseMenu.setMessage(menu);
                responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
                responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_NEXT_INSTALMENT_ENQUIRY);
            }
        }
//                 Enter loan account for repayment schedule
//        else if ((levels.length== 3 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"2"))) {
//            menu.append("Select Loan Account \n").append(musoniService.getClientAccountsList());
//        }
////                Show repayments schedule
//        else if ((levels.length== 4 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"2")&& !Objects.equals(levels[3],""))) {
//            String repaymentSchedule = enquiriesService.repaymentSchedule(musoniService.getClientLoans(user.get().getMusoniClientId()).get(Integer.parseInt(levels[3])-1));
//            menu.append("END ").append(repaymentSchedule);
//        }
//                 Enter loan account for collateral security check
        else if ((levels.length== 3 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"3"))) {
            List<String> loanStatement = musoniService.getClientLoans(user.get().getMusoniClientId());
            menu.append("Select Loan Account \n").append(musoniService.getClientAccountsList());
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_FIRST);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }
//                show collateral security
        else if ((levels.length== 4 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"3")&& !Objects.equals(levels[3],""))) {
            String collateralSecurity = enquiriesService.showCollateralSecurity(musoniService.getClientLoans(user.get().getMusoniClientId()).get(Integer.parseInt(levels[3])-1));
            menu.append("").append(collateralSecurity);
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }
        else if ((levels.length== 5 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"3")&& !Objects.equals(levels[3],"")&& Objects.equals(levels[4],"1"))) {
            String sendCollateralSecurity = enquiriesService.sendCollateralSecurityRequest(musoniService.getClientLoans(user.get().getMusoniClientId()).get(Integer.parseInt(levels[3])-1), phoneNumber);
            menu.append(" ").append(sendCollateralSecurity);
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_COLLATERAL);
        }
        else if ((levels.length== 5 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"3")&& !Objects.equals(levels[3],"")&& Objects.equals(levels[4],"2"))) {
            menu.append("Operation cancelled!..");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_COLLATERAL);
        }
//              Show All Loan Accounts
        else if ((levels.length== 3 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"4"))) {
            String loanAccounts = enquiriesService.showAllLoanAccounts(phoneNumber);
            menu.append("Loan Accounts:\n").append(loanAccounts);
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_COLLATERAL);
        }

//                ***End Enquiries***

//                ***start contact info***
        else if (levels.length==1 && Objects.equals(levels[0],"3")) {
            //Get Branch Menu
            menu.append("Requesting for Account Number")
                    .append("\n\nYour account number is ")
                    .append(user.get().getMusoniClientId())
                    .append(". Would you like us to share it via text message?\n")
                    .append("1. Yes\n")
                    .append("2. No, thank you.");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }else if(levels.length ==2 && Objects.equals(levels[1], "1")){

            String smsText = "Your Account Number is : " + user.get().getMusoniClientId() +
                    "\n\nYou can use it to access your account.\nUntu Capital Ltd";
            smsService.sendSingle(user.get().getPhoneNumber(), smsText);
            menu.append("Check your messages for the account number");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_REQUEST_ACCOUNT_NO);
        }else if(levels.length ==2 && Objects.equals(levels[1], "2")){
            menu.append("You have ended your session");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_REQUEST_ACCOUNT_NO);
        }

//                show branch details
        else if (levels.length==3 && Objects.equals(levels[1],"3")&& !Objects.equals(levels[2],"")) {
            String branchDetails = contactInfo.findBranchById(levels[2]);
            menu.append("Branch Details").append(branchDetails);
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_REQUEST_BRANCH_DETAILS);
        }
        else if (levels.length >= 1 && Objects.equals(levels[0],"4")) {
            responseMenu =userService.changePin(text,phoneNumber, transactionID);
        }
        else {
            menu.append("Invalid Option Try Again");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_APPLY_LOAN);
        }
//                ***start contact info***
        return responseMenu;
    }
    public void saveLoanApplication(String amount, String tenure, String phoneNumber){
        LoanApplication loanApplication = new LoanApplication();

        loanApplication.setAmount(Long.valueOf(amount));
        loanApplication.setTenure(Integer.parseInt(tenure));
        loanApplication.setPhoneNumber(phoneNumber);

        System.out.println(loanApplication);
        loanApplicationRepository.save(loanApplication);
    }

    public List<LoanApplication> getAllAppliedLoans(){
        return loanApplicationRepository.findAll();
    }
}
