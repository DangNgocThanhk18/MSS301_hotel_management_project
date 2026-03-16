package com.mss301.userservice.pojos;

import com.mss301.userservice.enums.AccountStatus;
import com.mss301.userservice.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_account")
@Data
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String passwordHash;

    private String fullName;

    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    private LocalDateTime createdAt;
}