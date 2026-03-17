package com.mss301.userservice.dto;

import com.mss301.userservice.enums.AccountStatus;
import com.mss301.userservice.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StaffResponseDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private AccountStatus status;
    private LocalDateTime createdAt;
}