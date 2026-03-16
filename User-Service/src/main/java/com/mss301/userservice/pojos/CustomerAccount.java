package com.mss301.userservice.pojos;

import com.mss301.userservice.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_account")
@Data
public class CustomerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String passwordHash;

    private String phone;

    private String fullName;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    private LocalDateTime createdAt;
}