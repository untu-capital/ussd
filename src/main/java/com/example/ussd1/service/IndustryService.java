package com.example.ussd1.service;

import com.example.ussd1.entity.Industry;
import com.example.ussd1.repository.IndustryRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class IndustryService {
    @Autowired
    private IndustryRepository industryRepository;

    public String getAllIndustries(){
        StringBuilder menu = new StringBuilder("");
        List<Industry> industries = industryRepository.findAll();
        for (Industry x:industries
             ) {
            menu.append("\n").append(x.getId()).append(".").append(x.getName());
        }
        return menu.toString();
    }

    public String findIndustryById(String id) {
        Optional<Industry> industry = industryRepository.findById(Integer.valueOf(id));
        return industry.get().getName();
    }
}
