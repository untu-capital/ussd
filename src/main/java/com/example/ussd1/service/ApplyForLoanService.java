package com.example.ussd1.service;

import com.example.ussd1.entity.LoanApplication;
import com.example.ussd1.entity.UserEntity;
import com.example.ussd1.repository.LoanApplicationRepository;
import com.example.ussd1.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
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

    public StringBuilder applyForLoanMenu(String text, String phoneNumber) throws ParseException {
        String branches = contactInfo.getAllBranches();
        String industries = industryService.getAllIndustries();
        Optional<UserEntity> user = userRepository.findByPhoneNumber(phoneNumber);
        StringBuilder menu = new StringBuilder();
        String[] levels = text.substring(text.indexOf(user.get().getPin())).split("\\*");

        System.out.println(text);
        System.out.println(levels.length);

        if(levels.length == 1){
            menu.append("CON What would you like to do ?\n1. Apply For A Loan.\n2. Account Enquiry");
        }
//                Apply for loan
        else if (levels.length == 2 && Objects.equals(levels[1], "1")) {
            menu.append("CON How much do you need ? (USD)");
        }
//                ***Start Apply For New Loan***
//        else if (levels.length == 3 && Objects.equals(levels[1], "1") && levels[2] != "") {
//            menu.append("CON How much do you need ? (US Dollars)");
//        }
//                ***End Apply For New Loan***

//                Start Apply For Repeat Loan
//        else if (levels.length==3 && Objects.equals(levels[1], "1") && Objects.equals(levels[2],"2")) {
//            menu.append("CON Enter Loan Amount (Repeat)");
//        }
//        To Do Validate Loan Amount Entered
        else if (levels.length == 3 && Objects.equals(levels[1], "1") && !Objects.equals(levels[2],"") && Integer.parseInt(levels[2]) < 2000 ) {
            menu.append("END Minimum loan amount must be at least $2,000");
        }

//            Start Apply For Repeat Loan
        else if (levels.length == 3 && Objects.equals(levels[1], "1") && !Objects.equals(levels[2],"") && Integer.parseInt(levels[2]) >= 2000) {
            menu.append("CON Select Repayment Period (Tenure).\n1.1-3 Months\n2.4-6 Months\n3.7-8 Months\n4.9-12 Months");
        }else if (levels.length == 4 && Objects.equals(levels[1], "1") && !Objects.equals(levels[2],"") && !Objects.equals(levels[3],"")) {
            menu.append("CON What industry sector matches your business?").append(industries);
        } else if (levels.length == 5 && Objects.equals(levels[1], "1") && !Objects.equals(levels[2],"")&& !Objects.equals(levels[3],"")&& !Objects.equals(levels[4],"")) {
            menu.append("CON Where are you currently located ?.");
        }else if (levels.length == 6 && Objects.equals(levels[1],"1") && !Objects.equals(levels[2],"")&& !Objects.equals(levels[3],"")&& !Objects.equals(levels[4],"")&& !Objects.equals(levels[5],"")) {
            String[] tenure = {"1-3 Months","4-6 Months","7-8 Months","9-12 Months"};
//            String branch = contactInfo.getBranchName(levels[5]);
            String industry = industryService.findIndustryById(levels[4]);
            menu.append("CON <b>Loan Application Details:</b>\nLoan Amount : $").append(levels[3]).append("\nTenure: ").append(tenure[Integer.parseInt(levels[3])-1]).append("\nIndustry Sector: ").append(industry).append("\nLocation: ").append(levels[5]).append("\n1. Confirm\n2. Cancel");
        }else if (levels.length == 7 && Objects.equals(levels[1],"1") && !Objects.equals(levels[2],"")&& !Objects.equals(levels[3],"")&& !Objects.equals(levels[4],"")&& !Objects.equals(levels[6],"")&& Objects.equals(levels[6],"1")) {
            //Save New Loan Application
            saveLoanApplication(levels,phoneNumber);
            menu.append("END Thank you for applying .Our Team will contact you soon");
        }else if (levels.length == 7 && !Objects.equals(levels[2],"")&& !Objects.equals(levels[3],"")&& !Objects.equals(levels[4],"")&& !Objects.equals(levels[5],"")&& Objects.equals(levels[6],"2")) {
            menu.append("END Thank you have a nice day!!!");
        }
//                ***End Apply For New Loan***

//                ***Start Enquiries***
        else if (levels.length==2 && Objects.equals(levels[1],"2")) {
            menu.append("CON Enquiries:\n1. My Loan Statement\n2. My Repayment Schedule\n3. Request for Collateral Securities");
        }
//                Enter loan account for checking balance
        else if ((levels.length== 3 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"1"))) {
            List<String> loanStatement = musoniService.getClientLoans(user.get().getMusoniClientId());
            menu.append("CON Select Loan Account \n").append(musoniService.getClientAccountsList());
        }
//                Query Mini Statement
        else if ((levels.length== 4 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"1")&& !Objects.equals(levels[3],""))) {
            String miniStatement = enquiriesService.getMiniStatement(musoniService.getClientLoans(user.get().getMusoniClientId()).get(Integer.parseInt(levels[3])-1));
            menu.append("END ").append(miniStatement);
        }
//                 Enter loan account for repayment schedule
        else if ((levels.length== 3 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"2"))) {
            menu.append("CON Select Loan Account \n").append(musoniService.getClientAccountsList());
        }
//                Show repayments schedule
        else if ((levels.length== 4 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"2")&& !Objects.equals(levels[3],""))) {
            String repaymentSchedule = enquiriesService.repaymentSchedule(musoniService.getClientLoans(user.get().getMusoniClientId()).get(Integer.parseInt(levels[3])-1));
            menu.append("END ").append(repaymentSchedule);
        }
//                 Enter loan account for collateral security check
        else if ((levels.length== 3 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"3"))) {
            List<String> loanStatement = musoniService.getClientLoans(user.get().getMusoniClientId());
            menu.append("CON Select Loan Account \n").append(musoniService.getClientAccountsList());
        }
//                show collateral security
        else if ((levels.length== 4 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"3")&& !Objects.equals(levels[3],""))) {
            String collateralSecurity = enquiriesService.showCollateralSecurity(musoniService.getClientLoans(user.get().getMusoniClientId()).get(Integer.parseInt(levels[3])-1));
            menu.append("CON ").append(collateralSecurity);
        }
        else if ((levels.length== 5 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"3")&& !Objects.equals(levels[3],"")&& Objects.equals(levels[4],"1"))) {
            String sendCollateralSecurity = enquiriesService.sendCollateralSecurityRequest(musoniService.getClientLoans(user.get().getMusoniClientId()).get(Integer.parseInt(levels[3])-1), phoneNumber);
            menu.append("END ").append(sendCollateralSecurity);
        }
        else if ((levels.length== 5 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"3")&& !Objects.equals(levels[3],"")&& Objects.equals(levels[4],"2"))) {
            menu.append("END Operation cancelled!..");
        }
//              Show All Loan Accounts
        else if ((levels.length== 3 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"4"))) {
            String loanAccounts = enquiriesService.showAllLoanAccounts(phoneNumber);
            menu.append("END Loan Accounts:\n").append(loanAccounts);
        }

//                ***End Enquiries***

//                ***start contact info***
        else if (levels.length==2 && Objects.equals(levels[1],"3")) {
            //Get Branch Menu
            menu.append("CON Select Branch").append(branches);
        }
//                show branch details
        else if (levels.length==3 && Objects.equals(levels[1],"3")&& !Objects.equals(levels[2],"")) {
            String branchDetails = contactInfo.findBranchById(levels[2]);
            menu.append("END Branch Details").append(branchDetails);
        }
        else {
            menu.append("CON Invalid Option Try Again");
        }
//                ***start contact info***
        return menu;
    }
    public void saveLoanApplication(String[] levels, String phoneNumber){
        LoanApplication loanApplication = new LoanApplication();
//        String branch = contactInfo.getBranchName(levels[6]);
        String industry = industryService.findIndustryById(levels[4]);

        System.out.println(levels[2]);
        loanApplication.setRepeatLoan(true);
        loanApplication.setAmount(Long.valueOf(levels[2]));
        loanApplication.setTenure(Integer.parseInt(levels[3]));
        loanApplication.setPhoneNumber(phoneNumber);
        loanApplication.setBranch(String.valueOf(levels[5]));
        loanApplication.setIndustrySector(industry);

        System.out.println(loanApplication);
        loanApplicationRepository.save(loanApplication);
    }

    public List<LoanApplication> getAllAppliedLoans(){
        return loanApplicationRepository.findAll();
    }
}
