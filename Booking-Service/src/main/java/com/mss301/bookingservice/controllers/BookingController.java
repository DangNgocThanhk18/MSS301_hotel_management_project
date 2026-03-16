package com.mss301.bookingservice.controllers;

import com.mss301.bookingservice.client.RoomClient;
import com.mss301.bookingservice.client.TaskClient;
import com.mss301.bookingservice.dto.BookingRequest;
import com.mss301.bookingservice.enums.ReservationRoomStatus;
import com.mss301.bookingservice.enums.ReservationStatus;
import com.mss301.bookingservice.pojos.Reservation;
import com.mss301.bookingservice.pojos.ReservationRoom;
import com.mss301.bookingservice.repository.ReservationRepository;
import com.mss301.bookingservice.repository.ReservationRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationRoomRepository reservationRoomRepository;

    @Autowired
    private RoomClient roomClient;

    @Autowired
    private TaskClient taskClient;

    // 1. DÀNH CHO KHÁCH HÀNG: Đặt phòng trực tuyến
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {

        for (Long roomId : request.getRoomIds()) {
            boolean isAvailable = roomClient.checkRoomAvailability(roomId);
            if (!isAvailable) {
                return ResponseEntity.badRequest().body("Phòng có ID " + roomId + " hiện không khả dụng!");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setCustomerId(request.getCustomerId());
        reservation.setExpectedCheckInDate(request.getExpectedCheckInDate());
        reservation.setExpectedCheckOutDate(request.getExpectedCheckOutDate());
        reservation.setNote(request.getNote());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedDate(new Date());

        reservation = reservationRepository.save(reservation);

        for (Long roomId : request.getRoomIds()) {
            ReservationRoom rr = new ReservationRoom();
            rr.setReservation(reservation);
            rr.setRoomId(roomId);
            rr.setStatus(ReservationRoomStatus.BOOKED);
            reservationRoomRepository.save(rr);
        }

        return ResponseEntity.ok("Đặt phòng thành công! Mã đơn của bạn là: " + reservation.getId());
    }

    // 2. DÀNH CHO LỄ TÂN: Xử lý Check-in
    @PutMapping("/{id}/check-in")
    public ResponseEntity<?> checkIn(@PathVariable Long id) {
        Optional<Reservation> resOptional = reservationRepository.findById(id);
        if (resOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reservation reservation = resOptional.get();
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setActualCheckInDate(new Date());

        reservationRepository.save(reservation);

        return ResponseEntity.ok("Check-in thành công cho mã đơn: " + id);
    }

    // 3. DÀNH CHO LỄ TÂN: Xử lý Check-out
    @PutMapping("/{id}/check-out")
    public ResponseEntity<?> checkOut(@PathVariable Long id) {
        Optional<Reservation> resOptional = reservationRepository.findById(id);
        if (resOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reservation reservation = resOptional.get();
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setActualCheckOutDate(new Date());

        reservationRepository.save(reservation);

        try {
            taskClient.createCleaningTask(reservation.getId());
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi yêu cầu dọn phòng: " + e.getMessage());
        }

        return ResponseEntity.ok("Check-out thành công. Đã báo lao công dọn phòng và đang tiến hành lập hóa đơn!");
    }
}