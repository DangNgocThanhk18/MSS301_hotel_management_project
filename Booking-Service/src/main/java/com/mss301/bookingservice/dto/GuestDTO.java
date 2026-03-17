// src/main/java/com/mss301/bookingservice/dto/GuestDTO.java
package com.mss301.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestDTO {
    private String fullName;
    private String email;
    private String phone;
    private String nationality;
    private String documentType;
    private String documentNumber;
}