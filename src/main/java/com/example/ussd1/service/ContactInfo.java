package com.example.ussd1.service;

import com.example.ussd1.entity.Branch;
import com.example.ussd1.repository.BranchRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ContactInfo {
    @Autowired
    private final BranchRepository branchRepository;

    public String addBranch(String branch){
        return "Add New Branch";
    }
    public String deleteBranch(String branchId){
        return "Delete Branch";
    }
    public String getAllBranches(){
        StringBuilder menu = new StringBuilder();
        List<Branch> branches = branchRepository.findAll();

        for (Branch x:branches
             ) {
            menu.append("\n").append(x.getId()).append(".").append(x.getBranchName());
        }
        return  menu.toString();
    }
    public String findBranchById(String id){
        Optional<Branch> branch = branchRepository.findById(Integer.valueOf(id));
        return ("\n<b> "+branch.get().getBranchName()+" Branch</b>\n<b>Address:</b> \n"+branch.get().getAddress()+"\n<b>Phone:</b> \n"+branch.get().getPhoneNumber());
    }
    public String getBranchName(String id){
        Optional<Branch> branch = branchRepository.findById(Integer.valueOf(id));
        return branch.get().getBranchName();
    }
}
