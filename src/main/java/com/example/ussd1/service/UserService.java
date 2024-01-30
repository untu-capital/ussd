package com.example.ussd1.service;

import com.example.ussd1.DTO.UserDTO;
import com.example.ussd1.entity.UserEntity;
import com.example.ussd1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    String[] levels;
    public Optional<UserEntity> findUserByPhoneNumber(String phoneNumber){
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public StringBuilder  registrationMenu(String text, String phoneNumber, String musoniClientId, String musoniFullname){
        levels = text.split("\\*");
        System.out.println(text);
        StringBuilder menu = new StringBuilder();

        if (text.isEmpty()) {
            // This is the first request. Note how we start the menu with CON
            menu.append("CON Dear %s. \nWelcome To Untu Capital Ltd. Press 1 to create a new account.\n1.Register \n2.Cancel".formatted(musoniFullname));

        }
//        Get user national id
        else if (text.contentEquals("1")){
            menu.append("CON Please enter your National ID number (in this format 12-345678Q12)");
        }
//        User Cancel Registration
        else if (text.contentEquals("2")){
            menu.append("END Thanks and have a great day.");
        }
////        Get user national id
//        else if (levels.length == 2){
//            menu.append("CON Please enter your National ID number (in this format 12345678Q12)");
//        }
//        Get user pin
        else if (levels.length==2) {
            menu.append("CON Please enter a 4-Digit Pin:");
        }
//        Verify user pin
        else if (levels.length==3) {
            menu.append("CON Please re-enter your PIN to confirm:");
        }
//        Validate user pin
        else if(levels.length==4 && Objects.equals(levels[2], levels[3])){
            menu.append(confirmDetails(levels, musoniFullname));
        }
//        Invalid pin
        else if(!Objects.equals(levels[2], levels[3])){

            if (levels.length==4){
                menu.append("CON Please Enter Matching 4-Digit Pin:");
            }else if(levels.length==5){
                menu.append("CON Please Re-Enter Matching 4-Digit Pin:");
            }
            else if (levels.length == 6 && !Objects.equals(levels[4], levels[5])){
                menu.append("END Dial Please Dail *261# enter valid details.");
            }
            else if (levels.length == 6 && Objects.equals(levels[4], levels[5])) {
                System.out.println("Length => "+levels.length);
                menu.append(confirmDetails(levels, musoniFullname));
            }else if (levels.length == 7 && Objects.equals(levels[6], "2") ) {
                menu.append("END Please dial *261# and enter correct details");
            }
            else if (levels.length == 7 && Objects.equals(levels[6], "1") ){
                menu.append(setDetails(levels, phoneNumber, musoniClientId, musoniFullname));
            }

        }
//        Set user Details
        else if (levels.length==5 && Objects.equals(levels[4], "1")) {
                menu.append(setDetails(levels, phoneNumber, musoniClientId, musoniFullname));
        }
//        Cancel try again
        else if (levels.length==5 && Objects.equals(levels[4], "2")) {
            menu.append("END Please dial *261# and enter correct details");
        }
        return menu;
    }
    public StringBuilder confirmDetails(String[] levels, String musoniFullname){
            StringBuilder menu = new StringBuilder();
            UserDTO userDTO = new UserDTO();
            userDTO.setFullName(musoniFullname);
            userDTO.setNationalId(levels[1]);
            menu.append("CON <b>Confirm your loan application details</b>  \n\n Full Name :").append(userDTO.getFullName()).append("\nNational Id:").append(userDTO.getNationalId()).append("\n1.Confirm \n2.Cancel Process");
            return menu;
    }
    public StringBuilder setDetails(String[] levels,String phoneNumber, String musoniClientId, String musoniFullname){
        StringBuilder menu = new StringBuilder();
        UserDTO userDTO = new UserDTO();
        userDTO.setPhoneNumber(phoneNumber);
        userDTO.setFullName(musoniFullname);
        userDTO.setNationalId(levels[1]);
        userDTO.setPin(levels[2]);
        userDTO.setMusoniClientId(musoniClientId);
        saveUser(userDTO);
        menu.append("END Thank you for registering. Dial *261# to login to your account.");
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
    public StringBuilder changePin(String text,String phoneNumber){
        StringBuilder menu = new StringBuilder();
        Optional<UserEntity> userExist = userRepository.findByPhoneNumber(phoneNumber);
        levels = text.split("\\*");
        System.out.println(Arrays.toString(levels));
        System.out.println("Length :"+levels.length);

        if (levels.length==1 && Objects.equals(levels[0], "2")){
            menu.append("CON Please enter your old pin:");
        }else if(levels.length ==2 && Objects.equals(levels[0], "2") && Objects.equals(userExist.get().getPin(), levels[1])){
            menu.append("CON Please enter new pin:");
        }else if(levels.length ==3 && Objects.equals(levels[0], "2") && Objects.equals(userExist.get().getPin(), levels[1])){
            menu.append("CON Please confirm new pin:");
        }else if(levels.length ==4 && Objects.equals(levels[0], "2") && levels[2].length() !=4 && levels[3].length() !=4){
            menu.append("END Please note that pin must have 4 digits. Dial *261# to try again.");
        }else if(levels.length ==4 && Objects.equals(levels[0], "2") && !Objects.equals(levels[2], levels[3])){
            menu.append("END Please enter matching pin:");
        }else if(levels.length ==4 && Objects.equals(levels[0], "2") && Objects.equals(levels[2], levels[3])){
            userExist.get().setPin(levels[3]);
            menu.append("END Pin changed successfully.");
        }
        else if(levels.length ==2 && Objects.equals(levels[0], "2") && !Objects.equals(userExist.get().getPin(), levels[1])){
            menu.append("END Please enter correct pin.");
        }
        return menu;
    }

}
