package com.example.ussd1.repository;

import com.example.ussd1.entity.CollateralSecurity;
import com.example.ussd1.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollateralSecurityRepository extends JpaRepository<CollateralSecurity,String> {
}
