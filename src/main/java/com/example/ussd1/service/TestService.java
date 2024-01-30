package com.example.ussd1.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class TestService {
    @Autowired
    private final UserService userService;
    @Autowired
    private final ApplyForLoanService applyForLoanService;
    @Autowired
    private final ContactInfo contactInfo;
    @Autowired final IndustryService industryService;

    @Autowired
    private final EnquiriesService enquiriesService;
//    public StringBuilder exist(String text, String phoneNumber){
//        String branches = contactInfo.getAllBranches();
//        String industries = industryService.getAllIndustries();
//        String[] levels = null;
//        levels = text.split("\\*");
//        StringBuilder menu = new StringBuilder("");
//
//            System.out.println("Array Length =>"+Integer.parseInt(levels[levels.length-1]));
//
//            if(levels.length == 1){
//                menu.append("CON What would you like to do ?\n1.APPLY FOR A LOAN.\n2.ENQUIRIES\n3.CONTACT INFO");
//            }
////                Apply for loan
//            else if (levels.length == 2 && Objects.equals(levels[1], "1")) {
//                menu.append("CON First time applying ?\n1.Yes (New).\n2.No (Repeat)");
//            }
////                ***Start Apply For New Loan***
//            else if (levels.length == 3 && Objects.equals(levels[1], "1") && Objects.equals(levels[2],"1")) {
//                menu.append("CON Enter Loan Amount");
//            }
////                ***End Apply For New Loan***
//
////                Start Apply For Repeat Loan
//            else if (levels.length==3 && Objects.equals(levels[1], "1") && Objects.equals(levels[2],"2")) {
//                menu.append("CON Enter Loan Amount (Repeat)");
//            }
////                Start Apply For Repeat Loan
//
//            else if (levels.length == 4 && Objects.equals(levels[1], "1") && !Objects.equals(levels[3],"")) {
//                System.out.println("Selected loan amount "+levels[3]);
//                menu.append("CON Repayment Period (Tenure)");
//            }else if (levels.length == 5 && Objects.equals(levels[1], "1") && !Objects.equals(levels[3],"") && !Objects.equals(levels[4],"")) {
//                menu.append("CON Select Nearest Branch.").append(industries);
//            } else if (levels.length == 6 && Objects.equals(levels[1], "1") && !Objects.equals(levels[3],"")&& !Objects.equals(levels[4],"")&& !Objects.equals(levels[5],"")) {
//                menu.append("CON Select Nearest Branch.").append(branches);
//            }else if (levels.length == 7 && Objects.equals(levels[1],"1") && !Objects.equals(levels[3],"")&& !Objects.equals(levels[4],"")&& !Objects.equals(levels[5],"")&& !Objects.equals(levels[6],"")) {
//                String branch = contactInfo.getBranchName(levels[6]);
//                String industry = industryService.findIndustryById(levels[5]);
//                menu.append("CON Confirm Loan Details:\nLoan Amount :$").append(levels[3]).append("\nTenure:").append(levels[4]).append("\nIndustry Sector: ").append(industry).append("\nNearest Branch: ").append(branch).append("\n1.Confirm\n2.Cancel");
//            }else if (levels.length == 8 && Objects.equals(levels[1],"1") && !Objects.equals(levels[3],"")&& !Objects.equals(levels[4],"")&& !Objects.equals(levels[5],"")&& !Objects.equals(levels[6],"")&& Objects.equals(levels[7],"1")) {
//                //Save New Loan Application
//                applyForLoanService.saveLoanApplication(levels,phoneNumber);
//                menu.append("END Thank you for applying loan.Our Branch will contact you soon");
//            }else if (levels.length == 8 && Objects.equals(levels[2],"1") && !Objects.equals(levels[3],"")&& !Objects.equals(levels[4],"")&& !Objects.equals(levels[5],"")&& !Objects.equals(levels[6],"")&& Objects.equals(levels[7],"2")) {
//                menu.append("END Thank you have a nice day!!!");
//            }
////                ***End Apply For New Loan***
//
////                ***Start Enquiries***
//            else if (levels.length==2 && Objects.equals(levels[1],"2")) {
//                menu.append("CON \n1.Loan Balance Enquiry\n2.Repayment Schedule\n3.Collateral Security Request\n4.Change Pin.\n5.Check Loan Accounts");
//            }
////                Enter loan account for checking balance
//            else if ((levels.length== 3 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"1"))) {
//                menu.append("CON Enter Loan Account No.");
//            }
////                Query Mini Statement
//            else if ((levels.length== 4 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"1")&& !Objects.equals(levels[3],""))) {
//                String miniStatement = enquiriesService.getMiniStatement(levels[3]);
//                menu.append("END ").append(miniStatement);
//            }
////                 Enter loan account for repayment schedule
//            else if ((levels.length== 3 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"2"))) {
//                menu.append("CON Enter Loan Account No.");
//            }
////                Show repayments schedule
//            else if ((levels.length== 4 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"2")&& !Objects.equals(levels[3],""))) {
//                String repaymentSchedule = enquiriesService.repaymentSchedule(levels[3]);
//                menu.append("END ").append(repaymentSchedule);
//            }
////                 Enter loan account for collateral security check
//            else if ((levels.length== 3 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"3"))) {
//                menu.append("CON Enter Loan Account No.");
//            }
////                show collateral security
//            else if ((levels.length== 4 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"3")&& !Objects.equals(levels[3],""))) {
//                String collateralSecurity = enquiriesService.showCollateralSecurity(levels[3]);
//                menu.append("END ").append(collateralSecurity);
//            }
////                Enter Old Pin
//            else if ((levels.length== 3 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"4"))) {
//                menu.append("CON Please Enter Old Pin.");
//            }
////                Enter New Pin
//            else if ((levels.length== 4 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"4")&& !Objects.equals(levels[3],""))) {
//                menu.append("CON Enter-New 4 Digit Pin");
//            }
////                        Re Enter New Pin
//            else if ((levels.length== 5 && Objects.equals(levels[1],"2")&& Objects.equals(levels[2],"4")&& !Objects.equals(levels[3],"")&& !Objects.equals(levels[4],""))) {
//                menu.append("CON Re-Enter New 4 Digit Pin");
//            }
////                Pin Change Confirmation
//            else if ((levels.length== 6 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"4")&& !Objects.equals(levels[3],"")&& !Objects.equals(levels[4],"") && !Objects.equals(levels[5],""))) {
//                menu.append("END Pin changed. Dial *261# to apply for loan");
//            }
////              Show All Loan Accounts
//            else if ((levels.length== 3 && Objects.equals(levels[1],"2") && Objects.equals(levels[2],"5"))) {
//                String loanAccounts = enquiriesService.showAllLoanAccounts(phoneNumber);
//                menu.append("CON Loan Accounts:\n").append(loanAccounts);
//            }
////                ***End Enquiries***
//
////                ***start contact info***
//            else if (levels.length==2 && Objects.equals(levels[1],"3")) {
//                //Get Branch Menu
//                menu.append("CON Select Branch").append(branches);
//            }
////                show branch details
//            else if (levels.length==3 && Objects.equals(levels[1],"3")&& !Objects.equals(levels[2],"")) {
//                String branchDetails = contactInfo.findBranchById(levels[2]);
//                menu.append("END Branch Details").append(branchDetails);
//            }
//            else {
//                menu.append("CON Invalid Option Try Again");
//            }
////                ***start contact info***
//        return menu;
//    }
}
