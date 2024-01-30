package com.example.ussd1.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "collateral_security")
@Data
public class CollateralSecurity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String phoneNumber ;
    private String loanAcc;
    private String clientName;
    private String collateralDescription;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
