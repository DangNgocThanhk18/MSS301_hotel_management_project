// src/main/java/com/mss301/bookingservice/dto/RoomBookingDTO.java
package com.mss301.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomBookingDTO {
    private Long reservationId;      // ID của reservation
    private Long roomId;              // ID của phòng
    private Date checkInDate;         // Ngày nhận phòng
    private Date checkOutDate;        // Ngày trả phòng
    private String status;            // Trạng thái (BOOKED, CHECKED_IN, etc.)
    private String guestName;         // Tên khách hàng
    private Integer adultCount;       // Số người lớn
    private Integer childCount;       // Số trẻ em
}
