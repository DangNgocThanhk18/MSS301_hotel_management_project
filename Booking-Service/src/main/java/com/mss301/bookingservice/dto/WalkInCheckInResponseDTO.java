// src/main/java/com/mss301/bookingservice/dto/WalkInCheckInResponseDTO.java
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
public class WalkInCheckInResponseDTO {
    private Long reservationId;
    private String reservationCode;
    private Long roomId;
    private String roomNumber;
    private String roomType;
    private String guestName;
    private Date checkInDate;
    private Date checkOutDate;
    private BigDecimal totalAmount;
    private BigDecimal deposit;
    private String message;
    private boolean success;
}