package com.example.ussd1.service;

import com.example.ussd1.entity.LoanApplication;
import com.example.ussd1.entity.UserEntity;
import com.example.ussd1.repository.BranchRepository;
import com.example.ussd1.repository.LoanApplicationRepository;
import com.example.ussd1.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class NonExistingClientsService {
    @Autowired
    private final ContactInfo contactInfo;
    @Autowired
    private final IndustryService industryService;

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    String[] levels;
    public Optional<UserEntity> findUserByPhoneNumber(String phoneNumber){
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public StringBuilder nonExistingClients(String text, String phoneNumber){
        String branches = contactInfo.getAllBranches();
        String industries = industryService.getAllIndustries();
        levels = text.split("\\*");
        System.out.println(text);
        StringBuilder menu = new StringBuilder();

        //TODO fix the # problem
        if(text.isEmpty() || text.equals("#")){
            menu.append("Good day,\n\nFor now, this service is only available for repeat clients");
        }
//                Apply for loan
        else if (levels.length == 1 && Objects.equals(levels[0], "1")) {
            menu.append("Contact any of our branches. ").append(branches);
        }
        //                show branch details
        else if (levels.length==2 && Objects.equals(levels[0],"1")&& !Objects.equals(levels[1],"")) {
//            String branch = contactInfo.getBranchName(levels[1]);
            String branchDetails = contactInfo.findBranchById(levels[1]);
            menu.append("END Branch Details").append(branchDetails);
        }

        else {
            menu.append("Invalid Option.. Try Again");
        }
        return menu;
    }
}
