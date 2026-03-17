package com.mss301.userservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    private String role;
}