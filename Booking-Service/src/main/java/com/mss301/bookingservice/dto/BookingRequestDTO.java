// src/main/java/com/mss301/bookingservice/dto/BookingRequestDTO.java
package com.mss301.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    private Long hotelId;
    private Long roomTypeId;
    private Date expectedCheckInDate;
    private Date expectedCheckOutDate;
    private String note;
    private GuestDTO guest; // Thông tin người đặt phòng
    private List<RoomOccupancy> rooms; // Thông tin các phòng

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomOccupancy {
        private Integer adultCount;
        private Integer childCount;
    }
}