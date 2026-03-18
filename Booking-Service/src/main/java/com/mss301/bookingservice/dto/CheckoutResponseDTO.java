// src/main/java/com/mss301/bookingservice/dto/CheckoutResponseDTO.java
package com.mss301.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponseDTO {
    private Long reservationId;
    private String reservationCode;
    private String guestName;
    private Long roomId;
    private String roomNumber;
    private Date checkInDate;
    private Date checkOutDate;
    private Long stayNights;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;      // Số tiền đã thanh toán (cọc)
    private BigDecimal remainingAmount; // Số tiền còn lại cần thanh toán
    private BigDecimal finalAmount;     // Tổng tiền thanh toán lúc checkout
    private String customerType;        // "REGISTERED" or "WALK_IN"
    private String message;
    private boolean success;
}
