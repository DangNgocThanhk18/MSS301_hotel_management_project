// src/main/java/com/mss301/bookingservice/dto/CheckoutRequestDTO.java
package com.mss301.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequestDTO {
    private Long reservationId;
    private Long receptionistId;
    private String paymentMethod; // CASH, BANK_TRANSFER, CREDIT_CARD
}