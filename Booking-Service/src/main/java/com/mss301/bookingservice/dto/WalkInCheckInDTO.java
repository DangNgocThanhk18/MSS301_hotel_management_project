// src/main/java/com/mss301/bookingservice/dto/WalkInCheckInDTO.java
package com.mss301.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalkInCheckInDTO {
    // Thông tin khách hàng
    private String guestName;
    private String email;
    private String phone;
    private String nationality;
    private String documentType;
    private String documentNumber;

    // Thông tin phòng
    private Long roomTypeId;
    private Integer adultCount;
    private Integer childCount;
    private Date checkInDate;
    private Date checkOutDate;

    // Thông tin lễ tân
    private Long receptionId;

    // Ghi chú
    private String notes;
}
