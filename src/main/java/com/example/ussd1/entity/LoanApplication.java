package com.example.ussd1.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Data
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String phoneNumber ;
    private Long amount;
    private int tenure;
    private String branch;
    private String industrySector;
    private boolean repeatLoan;
    private boolean viewed;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
