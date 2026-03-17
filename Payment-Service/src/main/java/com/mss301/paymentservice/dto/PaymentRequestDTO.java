// src/main/java/com/mss301/paymentservice/dto/PaymentRequestDTO.java
package com.mss301.paymentservice.dto;

import com.mss301.paymentservice.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String currency;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}