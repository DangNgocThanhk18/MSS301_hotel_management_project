// src/main/java/com/mss301/bookingservice/dto/BookingResponseDTO.java
package com.mss301.bookingservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class BookingResponseDTO {
    private Long reservationId;
    private String reservationCode;
    private String message;
    private BigDecimal totalAmount;
    private BigDecimal originalAmount;
    private Boolean weekendDiscountApplied;
    private BigDecimal weekendDiscountAmount;
    private BigDecimal discountAmount;
    private String voucherCode;
    private String voucherMessage;
    private Integer requiredRooms;
    private Integer totalAdults;
    private Integer totalChildren;
    private Long customerId;
    private Boolean isLoggedIn;
    private String customerEmail;
    private String customerName;
    private GuestBookingInfoDTO guestInfo;
    private List<RoomAllocationDTO> roomAllocations;

    @Data
    @Builder
    public static class GuestBookingInfoDTO {
        private String fullName;
        private String email;
        private String phone;
    }

    @Data
    @Builder
    public static class RoomAllocationDTO {
        private Integer roomNumber;
        private Long roomId;
        private Long roomTypeId;
        private Integer adultCount;
        private Integer childCount;
        private BigDecimal price;
    }
}