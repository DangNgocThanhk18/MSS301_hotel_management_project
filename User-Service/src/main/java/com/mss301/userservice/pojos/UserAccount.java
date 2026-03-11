package com.mss301.userservice.pojos;

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

    private String role;

    private String status;

    private LocalDateTime createdAt;
}
