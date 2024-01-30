package com.example.ussd1.controller;

import com.example.ussd1.entity.UserEntity;
import com.example.ussd1.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(value = "ussd")
@Slf4j
@RequiredArgsConstructor
public class AppController {
    @Autowired
    private final ContactInfo contactInfo;
    @Autowired
    private final IndustryService industryService;

    private final UserService userService;
    private final ApplyForLoanService applyForLoanService;
    private final EnquiriesService enquiriesService;
    private  final MusoniService musoniService;
    private final NonExistingClientsService nonExistingClientsService;

    @GetMapping
    public String helloUSSD(){
        return "You have reached `Untu Capital USSD callback link";
    }
    @GetMapping(value = "/get-loan-by-id/{id}",produces = "application/json")
    public ResponseEntity<String> getAllAppliedLoans(@PathVariable("id") String loanId){
        return new ResponseEntity<>( enquiriesService.setMusoniHeaders(loanId).toString(), HttpStatus.OK);
    }
    @PostMapping
    public String mainUSSD(@RequestParam("sessionId") String sessionId,
                           @RequestParam("serviceCode") String serviceCode,
                           @RequestParam("phoneNumber") String phoneNumber,
                           @RequestParam("text") String text){

        String[] levels = text.split("\\*");
        List<String> steps = List.of(text.split("\\*"));
        String branches = contactInfo.getAllBranches();
        String industries = industryService.getAllIndustries();

        StringBuilder response = new StringBuilder();
        Optional<UserEntity> userExist = userService.findUserByPhoneNumber(phoneNumber);

        try {
            if(userExist.isPresent()){
//                Level 1 Welcome Menu if user exist
                if (text.isEmpty()) {
                    response.append("CON Welcome to Untu Capital. Please select an option to proceed:").append("\n1. Loan Enquiry Services\n2. Reset Pin \n3. Get Help?");
                }
//                Level 2 enter pin to log in
                if(levels.length == 1 && Objects.equals(levels[0],"1")){
                    response.append("CON Please enter your pin to continue.");
                }
                if(levels.length >= 2 && Objects.equals(levels[0],"1") && !Objects.equals(userExist.get().getPin(), levels[1])){
                    int attempts = levels.length;
                    int chance = 5-attempts;
                    System.out.println("Chances =>"+chance);

//                    Check if the user enter password 3 times
                    if(chance > 0){
                        if (Objects.equals(userExist.get().getPin(), levels[attempts-1])){
                            response.append(applyForLoanService.applyForLoanMenu(text, phoneNumber));
                        }else {
                            response.append("CON Please Enter Correct Pin To Get Started (").append(chance).append(" chances left)");
                        }
                    }
//                    Check if the returned text contains correct pin
                    else if (steps.contains(userExist.get().getPin())){
                        response.append(applyForLoanService.applyForLoanMenu(text, phoneNumber));
                    }
//                   Lock Account for 5 minutes
                    else if(chance == 0){
                            response.append("END Wrong PIN entered. Account locked for 5 minutes.");
                    }
                }
                if(levels.length >= 2 && Objects.equals(levels[0],"1") && Objects.equals(userExist.get().getPin(), levels[1])) {
                    response.append(applyForLoanService.applyForLoanMenu(text, phoneNumber));
                }
//                Reset Pin Level
                if(levels.length == 1 && Objects.equals(levels[0],"2")){
                    response.append(userService.changePin(text,phoneNumber));
                }
                //                Get Help Level
                if(levels.length == 1 && Objects.equals(levels[0],"3")){
                    response.append("CON Contact any of our branches. ").append(branches);
                }
                if (levels.length == 2 && Objects.equals(levels[0], "3")&& !Objects.equals(levels[1],"")) {
                    String branchDetails = contactInfo.findBranchById(levels[1]);
                    response.append("END Branch Details").append(branchDetails);
                }

            }
//            ##########################################################              ELSE USER DOESN'T EXIT           #####################################################
            else{

                String mobileNo = "";
                String secondaryMobileNo = "";
                String clientId = "";

                String musoniClientObject = musoniService.getClientByMobileNo(phoneNumber, phoneNumber);
//                response.append("END musoniClientObject: " + musoniClientObject);

                if (musoniClientObject.length() <= 200){
                    response.append(nonExistingClientsService.nonExistingClients(text, phoneNumber));
                } else {
                    String musoniClientId = new String(new JSONObject(musoniClientObject).getBigInteger("id").toString());
                    String musoniFullname = new String(new JSONObject(musoniClientObject).getString("firstname") + " " + new JSONObject(musoniClientObject).getString("lastname"));
                    response.append(userService.registrationMenu(text, phoneNumber, musoniClientId, musoniFullname));
                }
//                response.append("END Musoni Client ID is" + userExistInMusoni);

//                if (musoniClientId.isEmpty()){
//
//
//                } else {
////                    response.append("END Proceed to registration id is " + musoniFullname);
//
//                }

            }
        }
        catch (Exception e){
            response.append("END Invalid Option Selected");
            log.error(String.valueOf(e));
        }

        return response.toString();
    }
}
