package com.mss301.userservice.dto;

import lombok.Data;

@Data
public class CustomerRequestDTO {
    private String fullName;
    private String email;
    private String phone;
    private String password;
}