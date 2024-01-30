package com.example.ussd1.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "industries")
@Data
public class Industry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
}
