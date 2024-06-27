package com.example.ussd1.service;

import com.example.ussd1.commons.UssdConstants;
import com.example.ussd1.dto.ResponseMenu;
import com.example.ussd1.dto.UserDTO;
import com.example.ussd1.dto.res.MessageResponse;
import com.example.ussd1.entity.UserEntity;
import com.example.ussd1.repository.UserRepository;
import com.example.ussd1.sessions.ChangePasswordSession;
import com.example.ussd1.sessions.ResetPasswordSession;
import com.example.ussd1.sessions.UserRegistrationSession;
import com.example.ussd1.sessions.UserSession;
import com.example.ussd1.util.RandomUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final SmsService smsService;

    String[] levels;
    public Optional<UserEntity> findUserByPhoneNumber(String phoneNumber){
        return userRepository.findByPhoneNumber(phoneNumber);
    }


    public ResponseMenu  registrationMenu(String text, String phoneNumber, String musoniClientId, String musoniFullname, String transactionID){

        ResponseMenu responseMenu = new ResponseMenu();
        levels = text.split("\\*");
        System.out.println(text);
        StringBuilder menu = new StringBuilder();

        String[] levels = UserRegistrationSession.getRegSession(transactionID);
        if (levels == null) {
            levels = text.split("\\*");
        } else {
            levels = Arrays.copyOf(levels, levels.length + 1);
            levels[levels.length - 1] = text;
        }
        UserRegistrationSession.setRegSession(transactionID, levels);

        log.info("LEVELS LENGTH-REG:{}",levels.length);
        log.info("LEVELS LAST INDEX-REG:{}",levels[levels.length-1]);
        log.info("LEVELS STRING-REG:{}",levels);

        if (text.equals("#")) {
            // This is the first request. Note how we start the menu with
             menu.append("Dear %s. \nWelcome To Untu Capital. Press 1 to register.\n1.Register \n2.Cancel".formatted(musoniFullname));
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_FIRST);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_REGISTER_USER);

        }
//        Get user national id
        else if (levels.length==2 && levels[1].contentEquals("1") ){
            menu.append("Please enter your National ID number (in this format 12-345678Q12)");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_REGISTER_USER);
        }
//        User Cancel Registration
        else if (levels.length==2 && levels[1].contentEquals("2")){
            menu.append("You have cancelled your session.");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_REGISTER_USER);
        }
////        Get user national id
//        else if (levels.length == 2){
//            menu.append("Please enter your National ID number (in this format 12345678Q12)");
//        }
//        Get user pin
        else if (levels.length==3) {
            menu.append("Please enter a 4-Digit Pin:");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }
//        Verify user pin
        else if (levels.length==4) {
            menu.append("Please re-enter your PIN to confirm:");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }
//        Validate user pin
        else if(levels.length==5 && Objects.equals(levels[3], levels[4])){
            menu.append(confirmDetails(levels, musoniFullname));
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);

        }
//        Invalid pin
        else if(!Objects.equals(levels[3], levels[4])){

            if (levels.length==5){
                menu.append("Please Enter Matching 4-Digit Pin:");
                responseMenu.setMessage(menu);
                responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
                responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);

            }else if(levels.length==6){
                menu.append("Please Re-Enter Matching 4-Digit Pin:");
                responseMenu.setMessage(menu);
                responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
                responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
            }
            else if (levels.length == 7 && !Objects.equals(levels[5], levels[6])){
                menu.append("Dial Please Dail *261# enter valid details.");
                responseMenu.setMessage(menu);
                responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
                responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_REGISTER_USER);
            }
            else if (levels.length == 8 && Objects.equals(levels[5], levels[6])) {
                System.out.println("Length => "+levels.length);
                menu.append(confirmDetails(levels, musoniFullname));
                responseMenu.setMessage(menu);
                responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
                responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);

            }else if (levels.length == 8 && Objects.equals(levels[7], "2") ) {
                menu.append("Please dial *261# and enter correct details");
                responseMenu.setMessage(menu);
                responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
                responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_REGISTER_USER);
            }
            else if (levels.length == 8 && Objects.equals(levels[7], "1") ){
                menu.append(setDetails(levels, phoneNumber, musoniClientId, musoniFullname));
                responseMenu.setMessage(menu);
                responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
                responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_REGISTER_USER);
            }

        }
//        Set user Details
        else if (levels.length==6 && Objects.equals(levels[5], "1")) {
                menu.append(setDetails(levels, phoneNumber, musoniClientId, musoniFullname));
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_REGISTER_USER);
        }
//        Cancel try again
        else if (levels.length==6 && Objects.equals(levels[5], "2")) {
            menu.append("You have entered wrong details too many times!.Please dial *261# and enter correct details");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_REGISTER_USER);
        }

        return responseMenu;
    }
    public StringBuilder confirmDetails(String[] levels, String musoniFullname){
            StringBuilder menu = new StringBuilder();
            UserDTO userDTO = new UserDTO();
            userDTO.setFullName(musoniFullname);
            userDTO.setNationalId(levels[2]);
            menu.append("Confirm your registration details  \n\nFull Name :").append(userDTO.getFullName()).append("\nNational Id:").append(userDTO.getNationalId()).append("\n1.Confirm \n2.Cancel Process");
            return menu;
    }
    public StringBuilder setDetails(String[] levels,String phoneNumber, String musoniClientId, String musoniFullname){

        StringBuilder menu = new StringBuilder();
        UserDTO userDTO = new UserDTO();
        userDTO.setPhoneNumber(phoneNumber);
        userDTO.setFullName(musoniFullname);
        userDTO.setNationalId(levels[2]);
        userDTO.setPin(levels[3]);
        userDTO.setMusoniClientId(musoniClientId);
        saveUser(userDTO);
        menu.append("Thank you for registering. Dial *261# to login to your account.");

        return menu;
    }
    public void saveUser(UserDTO userDTO){
        UserEntity user = new UserEntity();
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setFullName(userDTO.getFullName());
        user.setPin(userDTO.getPin());
        user.setNationalId(userDTO.getNationalId());
        user.setMusoniClientId(userDTO.getMusoniClientId());

        userRepository.save(user);
    }
    @Transactional
    public ResponseMenu changePin(String text,String phoneNumber, String transactionID){

        StringBuilder menu = new StringBuilder();
        ResponseMenu responseMenu = new ResponseMenu();
        Optional<UserEntity> userExist = userRepository.findByPhoneNumber(phoneNumber);
        levels = text.split("\\*");

        String[] levels = ChangePasswordSession.getChangePasswordMapSession(transactionID);
        if (levels == null) {
            levels = text.split("\\*");
        } else {
            levels = Arrays.copyOf(levels, levels.length + 1);
            levels[levels.length - 1] = text;
        }
        ChangePasswordSession.setChangePasswordMapSession(transactionID, levels);

        log.info("LEVEL LENGTH-CHANGE_PIN: {}",levels.length);
        log.info("LEVEL VALUE-CHANGE_PIN: {}",Arrays.toString(levels));
        log.info("LEVEL LAST_INDEX-CHANGE_PIN: {}",levels[levels.length-1]);

        if (levels.length==1 && Objects.equals(levels[0], "4")){
            menu.append("Please enter your old pin:");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_CHANGE_PASSWORD);
        }else if(levels.length ==2 && Objects.equals(levels[0], "4") && Objects.equals(userExist.get().getPin(), levels[1])){
            menu.append("Please enter new pin:");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }else if(levels.length ==3 && Objects.equals(levels[0], "4") && !Objects.equals(userExist.get().getPin(), levels[2])){
            menu.append("Please confirm new pin:");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_PENDING);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }else if(levels.length ==4 && Objects.equals(levels[0], "4") && levels[2].length() !=4 && levels[3].length() !=4){
            menu.append("Please note that pin must have 4 digits. Dial *261# to try again.");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_CHANGE_PASSWORD);
        }else if(levels.length ==4 && Objects.equals(levels[0], "4") && !Objects.equals(levels[2], levels[3])){
            menu.append("Please enter matching pin:");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_CHANGE_PASSWORD);
        }else if(levels.length ==4 && Objects.equals(levels[0], "4") && Objects.equals(levels[2], levels[3])){
            userExist.get().setPin(levels[3]);
            userRepository.save(userExist.get());
            menu.append("END Pin changed successfully.");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_CHANGE_PASSWORD);
        }
        else if(levels.length ==2 && Objects.equals(levels[0], "") && !Objects.equals(userExist.get().getPin(), levels[1])){
            menu.append("END Please enter correct pin.");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_CHANGE_PASSWORD);
        }
        return responseMenu;
    }

    @Transactional
    public ResponseMenu resetPin(String text,String phoneNumber, String transactionID){

        ResponseMenu responseMenu = new ResponseMenu();
        levels = text.split("\\*");
        System.out.println(text);
        StringBuilder menu = new StringBuilder();

        String[] levels = ResetPasswordSession.getResetPasswordSessionMap(transactionID);
        if (levels == null) {
            levels = text.split("\\*");
        } else {
            levels = Arrays.copyOf(levels, levels.length + 1);
            levels[levels.length - 1] = text;
        }
        ResetPasswordSession.setResetPasswordSessionMap(transactionID, levels);

        log.info("LEVELS LENGTH:{}",levels.length);
        log.info("LEVELS LAST INDEX:{}",levels[levels.length-1]);
        log.info("LEVELS STRING:{}",levels);

        if (text.equals("2") && levels.length==1) {
            menu.append("Press 1 to confirm if you want to reset your password. \n1. Confirm \n2. Cancel");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_FIRST);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_MENU_PROCESSING);
        }
        else if (levels.length==2 && levels[1].contentEquals("1") ){

            Optional<UserEntity> userExist = userRepository.findByPhoneNumber(phoneNumber);
            UserEntity user = userExist.get();
            String pin = RandomUtil.generateCode(4);
            user.setPin(pin);
            userRepository.save(user);

            String smsText = "Your new password is : " + pin +
                    "\n\nYou can use it to login to your account.\nUntu Capital Ltd";
            smsService.sendSingle(user.getPhoneNumber(), smsText);

            menu.append("Pin changed successfully. Please check your messages for the new pin");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_RESET_PASSWORD);

        }else if(levels.length==2 && levels[1].contentEquals("2")){
            menu.append("You have ended your session.");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_RESET_PASSWORD);
        }else {
            menu.append("Invalid Option.");
            responseMenu.setMessage(menu);
            responseMenu.setStage(UssdConstants.STAGE_MENU_COMPLETE);
            responseMenu.setTransactionType(UssdConstants.TRANSACTION_TYPE_RESET_PASSWORD);
        }
       return responseMenu;
    }

}
