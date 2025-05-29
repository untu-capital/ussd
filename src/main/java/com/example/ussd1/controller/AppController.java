package com.example.ussd1.controller;

import com.example.ussd1.commons.UssdConstants;
import com.example.ussd1.dto.ResponseMenu;
import com.example.ussd1.dto.req.MessageRequest;
import com.example.ussd1.dto.res.MessageResponse;
import com.example.ussd1.entity.UserEntity;
import com.example.ussd1.service.*;
import com.example.ussd1.sessions.SessionId;
import com.example.ussd1.sessions.UserLoginSession;
import com.example.ussd1.sessions.UserSession;
import com.example.ussd1.util.DateUtil;
import com.example.ussd1.util.TransactionIdGenerator;
import com.example.ussd1.util.XmlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Arrays;
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
    private final MusoniService musoniService;
    private final NonExistingClientsService nonExistingClientsService;

    @GetMapping
    public String helloUSSD() {
        return "You have reached Untu Capital USSD callback link";
    }

    @GetMapping(value = "/get-loan-by-id/{id}", produces = "application/xml")
    public ResponseEntity<String> getAllAppliedLoans(@PathVariable("id") String loanId) {
        String jsonResponse = enquiriesService.setMusoniHeaders(loanId).toString();
        String xmlResponse = XmlUtil.convertToXml(jsonResponse);
        return new ResponseEntity<>(xmlResponse, HttpStatus.OK);
    }

    @PostMapping(consumes = "application/xml", produces = "application/xml")
    public ResponseEntity<String> mainUSSD(@RequestBody MessageRequest messageRequest) throws JAXBException, ParseException {
        String transactionID = messageRequest.getTransactionID();
        String channel = messageRequest.getChannel();
        String sourceNumber = messageRequest.getSourceNumber();
        String phoneNumber = messageRequest.getSourceNumber();
        String text = messageRequest.getMessage();

        log.info("Received USSD request: transactionID={}, channel={}, sourceNumber={}, text={}", transactionID, channel, sourceNumber, text);

        String[] levels = UserSession.getSession(transactionID);
        if (levels == null) {
            levels = text.split("\\*");
        } else {
            levels = Arrays.copyOf(levels, levels.length + 1);
            levels[levels.length - 1] = text;
        }
        UserSession.setSession(transactionID, levels);

        log.info("Parsed levels: {}", Arrays.toString(levels));
        log.info("LEVEL LAST INDEX:{}", levels[levels.length - 1]);
        log.info("LEVEL LENGTH:{}", levels.length);
        MessageResponse messageResponse = new MessageResponse();

        StringBuilder response = new StringBuilder();
        Optional<UserEntity> userExist = userService.findUserByPhoneNumber(phoneNumber);

        if (!userExist.isPresent()) {
            log.info("User not found for phone number: {}", phoneNumber);

            String musoniClientObject = musoniService.getClientByMobileNo(phoneNumber, phoneNumber);
            log.info("Musoni client response: {}", musoniClientObject);

            if (musoniClientObject.length() <= 200) {
                ResponseMenu responseMenu =nonExistingClientsService.nonExistingClients(text, phoneNumber);
                response.append(responseMenu.getMessage());
                messageResponse.setStage(responseMenu.getStage());
                messageResponse.setTransactionType(responseMenu.getTransactionType());
            } else {
                String musoniClientId = new JSONObject(musoniClientObject).getBigInteger("id").toString();
                String musoniFullname = new JSONObject(musoniClientObject).getString("firstname") + " " + new JSONObject(musoniClientObject).getString("lastname");
                ResponseMenu responseMenu = userService.registrationMenu(text, phoneNumber, musoniClientId, musoniFullname, transactionID);
                response.append(responseMenu.getMessage());
                messageResponse.setStage(responseMenu.getStage());
                messageResponse.setTransactionType(responseMenu.getTransactionType());
            }
        } else {
            UserEntity user = userExist.get();
            log.info("User found: {}", user);

            if (text.equals("#")) {
                response.append("Welcome to Untu Capital. Please select an option to proceed:")
                        .append("\n1. Login ")
                        .append("\n2. Reset Pin ");
                messageResponse.setStage(UssdConstants.STAGE_MENU_PENDING);
            } else if (levels.length == 2 && levels[1].equals("1")) {
                response.append("Please enter your pin.");
                messageResponse.setStage(UssdConstants.STAGE_MENU_PENDING);
            } else if (levels.length >= 3 && levels[1].equals("1")) {

                String enteredPin = levels[levels.length - 1];
//                String enteredPin = "1234";
                String storedPin = user.getPin();
                boolean validated = UserLoginSession.getLoginSession(transactionID);
                if (!validated) {
                    if (enteredPin.equals(storedPin)) {
                        UserLoginSession.setLoginSession(transactionID, true);
                    }
                }

                log.info("Entered PIN: {}, Stored PIN: {}", enteredPin, storedPin);

                if (UserLoginSession.getLoginSession(transactionID)) {

                    ResponseMenu loanMenuResponse = applyForLoanService.applyForLoanMenu(text, phoneNumber, levels, transactionID);
                    log.info("Loan menu response: {}", loanMenuResponse);
                    response.append(loanMenuResponse.getMessage());
//
                    messageResponse.setStage(loanMenuResponse.getStage());
                    messageResponse.setTransactionType(loanMenuResponse.getTransactionType());
                } else {
                    int attempts = levels.length - 2; // -2 to account for the first two levels
                    int chancesLeft = 3 - attempts;

                    log.info("Incorrect PIN entered. Attempts: {}, Chances left: {}", attempts, chancesLeft);

                    if (chancesLeft > 0) {
                        response.append("Incorrect pin. Please enter correct pin to get started (")
                                .append(chancesLeft)
                                .append(" chances left)");
                        messageResponse.setStage(UssdConstants.STAGE_MENU_PENDING);
                    } else if (chancesLeft == 0) {
                        response.append("You have entered an incorrect PIN too many times. Please reset your PIN to regain access.");
                        messageResponse.setStage(UssdConstants.STAGE_MENU_COMPLETE);
                    }
                }
            } else if (levels.length >= 2 && levels[1].equals("2")) {
                ResponseMenu responseMenu =userService.resetPin(text, phoneNumber, transactionID);
                response.append(responseMenu.getMessage());
                messageResponse.setTransactionType(responseMenu.getTransactionType());
                messageResponse.setStage(responseMenu.getStage());
            } else if (levels.length == 1 && levels[0].equals("3")) {
                response.append("Contact any of our branches. ");
                messageResponse.setStage(UssdConstants.STAGE_MENU_PENDING);
            } else if (levels.length == 2 && levels[0].equals("3") && !levels[1].isEmpty()) {
                String branchDetails = contactInfo.findBranchById(levels[1]);
                response.append("Branch Details").append(branchDetails);
                messageResponse.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            }
        }


        String applicationTransactionID = SessionId.getSession(transactionID);

        if(applicationTransactionID==null) {

                SessionId.setSession(transactionID, TransactionIdGenerator.generateApplicationTransactionID());

        }

        messageResponse.setTransactionTime(DateUtil.formatCurrentDate());
        messageResponse.setTransactionID(messageRequest.getTransactionID());
        messageResponse.setSourceNumber(messageRequest.getDestinationNumber());
        messageResponse.setDestinationNumber(messageRequest.getSourceNumber());
        messageResponse.setMessage(response.toString());

        if (messageResponse.getStage() == null) {
            messageResponse.setStage(UssdConstants.STAGE_MENU_PENDING);
        }

        messageResponse.setChannel(messageRequest.getChannel());
        messageResponse.setApplicationTransactionID(applicationTransactionID);

        if (messageResponse.getTransactionType() == null) {
            messageResponse.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }

        log.info("MessageResponse before marshalling: {}", messageResponse);

        JAXBContext jaxbContext = JAXBContext.newInstance(MessageResponse.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter sw = new StringWriter();
        marshaller.marshal(messageResponse, sw);

        String xmlResponse = sw.toString();

        log.info("Final XML Response: {}", xmlResponse);

        return new ResponseEntity<>(xmlResponse, HttpStatus.OK);
    }
}
