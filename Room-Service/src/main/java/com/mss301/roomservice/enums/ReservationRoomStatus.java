// src/main/java/com/mss301/roomservice/enums/ReservationRoomStatus.java
package com.mss301.roomservice.enums;

public enum ReservationRoomStatus {
    BOOKED,       // Đã đặt
    CHECKED_IN,   // Đã nhận phòng
    CHECKED_OUT,  // Đã trả phòng
    CANCELLED,    // Đã hủy
    NO_SHOW       // Không đến
}