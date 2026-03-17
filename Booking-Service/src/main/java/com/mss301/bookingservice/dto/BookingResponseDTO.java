// src/main/java/com/mss301/bookingservice/dto/BookingResponseDTO.java
package com.mss301.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long reservationId;
    private String reservationCode;
    private String message;
    private BigDecimal totalAmount;
    private Integer requiredRooms;
    private Integer totalAdults;
    private Integer totalChildren;
    private Long customerId;
    private Boolean isLoggedIn;
    private String customerEmail;
    private String customerName;
    private GuestBookingInfoDTO guestInfo; // ĐỔI TÊN Ở ĐÂY
    private List<RoomAllocationDTO> roomAllocations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomAllocationDTO {
        private Integer roomNumber;
        private Long roomId;
        private Long roomTypeId;
        private Integer adultCount;
        private Integer childCount;
        private BigDecimal price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuestBookingInfoDTO { // ĐỔI TÊN INNER CLASS
        private String fullName;
        private String email;
        private String phone;
        // Chỉ lấy 3 thông tin cơ bản cho response
    }
}