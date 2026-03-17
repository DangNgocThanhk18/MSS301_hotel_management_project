// src/main/java/com/mss301/paymentservice/dto/PaymentResponseDTO.java
package com.mss301.paymentservice.dto;

import com.mss301.paymentservice.enums.PaymentMethod;
import com.mss301.paymentservice.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private Long reservationId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionRef;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private String paymentUrl; // Cho VNPay
}