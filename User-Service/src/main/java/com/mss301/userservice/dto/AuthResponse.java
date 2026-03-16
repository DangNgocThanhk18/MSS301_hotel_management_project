package com.mss301.userservice.dto;

import com.mss301.userservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String message;

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private UserRole role;
    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }
}