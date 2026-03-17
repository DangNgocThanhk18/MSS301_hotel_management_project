package com.mss301.roomservice.controllers;

import com.mss301.roomservice.enums.ReservationRoomStatus;
import com.mss301.roomservice.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/room-bookings")
@RequiredArgsConstructor
public class RoomBookingController {

    private final RoomService roomService;

    /**
     * API để Booking Service gọi khi có reservation mới
     * POST /api/room-bookings
     */
    @PostMapping
    public ResponseEntity<?> createRoomBooking(@RequestBody RoomBookingRequest request) {
        try {
            roomService.bookRoom(
                    request.getReservationId(),
                    request.getRoomId(),
                    request.getCheckInDate(),
                    request.getCheckOutDate()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Room booked successfully",
                    "roomId", request.getRoomId(),
                    "reservationId", request.getReservationId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * API để Booking Service gọi khi hủy reservation
     * DELETE /api/room-bookings/{reservationId}
     */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> cancelRoomBooking(@PathVariable Long reservationId) {
        // Logic hủy đặt phòng
        return ResponseEntity.ok(Map.of("message", "Booking cancelled for reservation: " + reservationId));
    }

    @Data
    public static class RoomBookingRequest {
        private Long reservationId;
        private Long roomId;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date checkInDate;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date checkOutDate;
    }
    // Trong RoomBookingController.java
    @PutMapping("/{reservationId}/status")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long reservationId,
            @RequestParam("status") String status) {
        try {
            ReservationRoomStatus newStatus = ReservationRoomStatus.valueOf(status.toUpperCase());
            roomService.updateRoomBookingStatus(reservationId, newStatus);
            return ResponseEntity.ok(Map.of(
                    "message", "Booking status updated successfully",
                    "reservationId", reservationId,
                    "status", newStatus
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid status: " + status
            ));
        }
    }
}