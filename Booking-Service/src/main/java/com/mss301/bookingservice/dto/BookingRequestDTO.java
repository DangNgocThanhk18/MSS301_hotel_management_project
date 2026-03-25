// src/main/java/com/mss301/bookingservice/dto/BookingRequestDTO.java
package com.mss301.bookingservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class BookingRequestDTO {
    private Long hotelId;
    private Long roomTypeId;
    private Date expectedCheckInDate;
    private Date expectedCheckOutDate;
    private String note;
    private String voucherCode;
    private Long customerId;  // Thêm trường này để nhận từ frontend
    private List<RoomOccupancy> rooms;
    private GuestInfo guest;

    @Data
    public static class RoomOccupancy {
        private Integer adultCount;
        private Integer childCount;
    }

    @Data
    public static class GuestInfo {
        private String fullName;
        private String email;
        private String phone;
        private String nationality;
        private String documentType;
        private String documentNumber;
    }
}