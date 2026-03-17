package com.mss301.userservice.dto;

import com.mss301.userservice.enums.UserRole;
import lombok.Data;

@Data
public class StaffRequestDTO {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
}