// src/main/java/com/mss301/bookingservice/client/RoomClient.java
package com.mss301.bookingservice.client;

import com.mss301.bookingservice.dto.RoomTypeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@FeignClient(name = "room-service", url = "${room.service.url}/api")
public interface RoomClient {

    @GetMapping("/room-type/{id}")
    RoomTypeDTO getRoomTypeById(@PathVariable("id") Long id);

    @GetMapping("/rooms/available")
    List<Long> findAvailableRooms(
            @RequestParam("roomTypeId") Long roomTypeId,
            @RequestParam("checkIn") String checkIn,
            @RequestParam("checkOut") String checkOut,
            @RequestParam("count") int count);

    // THÊM method mới để đặt phòng
    @PostMapping("/room-bookings")
    void bookRoom(@RequestBody RoomBookingRequest request);
    @PutMapping("/room-bookings/{reservationId}/status")
    void updateBookingStatus(@PathVariable("reservationId") Long reservationId,
                             @RequestParam("status") String status);
    // Inner class cho request
    class RoomBookingRequest {
        private Long reservationId;
        private Long roomId;
        private Date checkInDate;
        private Date checkOutDate;

        // Getters and Setters
        public Long getReservationId() { return reservationId; }
        public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }

        public Date getCheckInDate() { return checkInDate; }
        public void setCheckInDate(Date checkInDate) { this.checkInDate = checkInDate; }

        public Date getCheckOutDate() { return checkOutDate; }
        public void setCheckOutDate(Date checkOutDate) { this.checkOutDate = checkOutDate; }
    }
}