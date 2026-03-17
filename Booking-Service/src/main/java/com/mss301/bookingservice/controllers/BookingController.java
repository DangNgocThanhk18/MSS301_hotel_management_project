// src/main/java/com/mss301/bookingservice/controllers/BookingController.java
package com.mss301.bookingservice.controllers;

import com.mss301.bookingservice.client.RoomClient;
import com.mss301.bookingservice.client.TaskClient;
import com.mss301.bookingservice.client.UserClient;
import com.mss301.bookingservice.dto.*;
import com.mss301.bookingservice.enums.ReservationRoomStatus;
import com.mss301.bookingservice.enums.ReservationStatus;
import com.mss301.bookingservice.pojos.Guest;
import com.mss301.bookingservice.pojos.Reservation;
import com.mss301.bookingservice.pojos.ReservationRoom;
import com.mss301.bookingservice.repository.GuestRepository;
import com.mss301.bookingservice.repository.ReservationRepository;
import com.mss301.bookingservice.repository.ReservationRoomRepository;
import com.mss301.bookingservice.service.RoomCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final GuestRepository guestRepository;
    private final RoomClient roomClient;
    private final TaskClient taskClient;
    private final UserClient userClient;
    private final RoomCalculationService calculationService;

    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody BookingRequestDTO request) {

        log.info("Received booking request with {} rooms", request.getRooms().size());

        try {
            // 1. Kiểm tra token nếu có
            Long customerId = null;
            String customerEmail = null;
            String customerName = null;

            if (token != null && !token.isEmpty()) {
                try {
                    UserInfoDTO user = userClient.getUserByToken("Bearer " + token);
                    if (user != null) {
                        customerId = user.getId();
                        customerEmail = user.getEmail();
                        customerName = user.getFullName();
                        log.info("User logged in: {} - {}", customerId, customerEmail);
                    }
                } catch (Exception e) {
                    log.warn("Invalid token, proceeding as guest: {}", e.getMessage());
                }
            }

            // 2. Lấy thông tin Room Type
            RoomTypeDTO roomType = roomClient.getRoomTypeById(request.getRoomTypeId());

            // 3. Kiểm tra số phòng
            boolean isValidRooms = calculationService.validateRooms(
                    request.getRooms(), roomType.getCapacity());

            if (!isValidRooms) {
                int requiredRooms = calculationService.calculateRequiredRooms(
                        request.getRooms(), roomType.getCapacity());
                return ResponseEntity.badRequest().body(Map.of(
                        "message", String.format(
                                "Số phòng không hợp lệ. Với %d người lớn và %d trẻ em, cần %d phòng",
                                calculationService.getTotalAdults(request.getRooms()),
                                calculationService.getTotalChildren(request.getRooms()),
                                requiredRooms)
                ));
            }

            // 4. Kiểm tra availability
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String checkInStr = dateFormat.format(request.getExpectedCheckInDate());
            String checkOutStr = dateFormat.format(request.getExpectedCheckOutDate());

            List<Long> availableRooms = roomClient.findAvailableRooms(
                    request.getRoomTypeId(), checkInStr, checkOutStr, request.getRooms().size());

            if (availableRooms.size() < request.getRooms().size()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Không đủ phòng trống. Chỉ còn " + availableRooms.size() + " phòng."
                ));
            }

            // 5. Tính tổng tiền
            long nights = (request.getExpectedCheckOutDate().getTime() -
                    request.getExpectedCheckInDate().getTime()) / (1000 * 60 * 60 * 24);

            BigDecimal totalAmount = roomType.getBasePrice()
                    .multiply(BigDecimal.valueOf(nights))
                    .multiply(BigDecimal.valueOf(request.getRooms().size()));

            // 6. Tạo Reservation
            Reservation reservation = new Reservation();
            reservation.setReservationCode("RES-" + System.currentTimeMillis());
            reservation.setCustomerId(customerId);
            reservation.setHotelId(request.getHotelId());
            reservation.setExpectedCheckInDate(request.getExpectedCheckInDate());
            reservation.setExpectedCheckOutDate(request.getExpectedCheckOutDate());
            reservation.setNote(request.getNote());
            reservation.setTotalAmount(totalAmount);
            reservation.setStatus(ReservationStatus.PENDING);
            reservation.setCreatedDate(new Date());

            reservation = reservationRepository.save(reservation);

            // 7. Tạo Guest (người đặt phòng)
            Guest guest = new Guest();
            guest.setFullName(request.getGuest().getFullName());
            guest.setEmail(request.getGuest().getEmail());
            guest.setPhone(request.getGuest().getPhone());
            guest.setNationality(request.getGuest().getNationality());
            guest.setDocumentType(request.getGuest().getDocumentType());
            guest.setDocumentNumber(request.getGuest().getDocumentNumber());
            guest.setReservation(reservation);

            guestRepository.save(guest);

            // 8. Tạo các ReservationRoom
            List<BookingResponseDTO.RoomAllocationDTO> allocations = new ArrayList<>();
            List<RoomClient.RoomBookingRequest> roomBookings = new ArrayList<>(); // THÊM

            for (int i = 0; i < request.getRooms().size(); i++) {
                BookingRequestDTO.RoomOccupancy occupancy = request.getRooms().get(i);
                Long roomId = availableRooms.get(i);

                ReservationRoom reservationRoom = new ReservationRoom();
                reservationRoom.setReservation(reservation);
                reservationRoom.setRoomId(roomId);
                reservationRoom.setRoomTypeId(request.getRoomTypeId());
                reservationRoom.setNightlyPrice(roomType.getBasePrice());
                reservationRoom.setStatus(ReservationRoomStatus.BOOKED);
                reservationRoom.setAdultCount(occupancy.getAdultCount());
                reservationRoom.setChildCount(occupancy.getChildCount());

                reservationRoomRepository.save(reservationRoom);

                // THÊM: Chuẩn bị dữ liệu để gọi Room Service
                RoomClient.RoomBookingRequest roomBooking = new RoomClient.RoomBookingRequest();
                roomBooking.setReservationId(reservation.getId());
                roomBooking.setRoomId(roomId);
                roomBooking.setCheckInDate(request.getExpectedCheckInDate());
                roomBooking.setCheckOutDate(request.getExpectedCheckOutDate());
                roomBookings.add(roomBooking);

                allocations.add(BookingResponseDTO.RoomAllocationDTO.builder()
                        .roomNumber(i + 1)
                        .roomId(roomId)
                        .roomTypeId(request.getRoomTypeId())
                        .adultCount(occupancy.getAdultCount())
                        .childCount(occupancy.getChildCount())
                        .price(roomType.getBasePrice())
                        .build());
            }

// ===== THÊM: Gọi Room Service để cập nhật trạng thái phòng =====
            try {
                for (RoomClient.RoomBookingRequest roomBooking : roomBookings) {
                    roomClient.bookRoom(roomBooking);
                    log.info("Đã gửi thông tin đặt phòng sang Room Service: roomId={}, reservationId={}",
                            roomBooking.getRoomId(), roomBooking.getReservationId());
                }
            } catch (Exception e) {
                log.error("Lỗi khi gọi Room Service cập nhật trạng thái phòng: {}", e.getMessage());
                // Vẫn tiếp tục vì booking đã thành công trong DB của mình
            }

// 9. Tạo response (giữ nguyên)

            // 9. Tạo response
            BookingResponseDTO response = BookingResponseDTO.builder()
                    .reservationId(reservation.getId())
                    .reservationCode(reservation.getReservationCode())
                    .message(customerId != null
                            ? "Đặt phòng thành công (Đã đăng nhập)"
                            : "Đặt phòng thành công (Khách vãng lai)")
                    .totalAmount(totalAmount)
                    .requiredRooms(request.getRooms().size())
                    .totalAdults(calculationService.getTotalAdults(request.getRooms()))
                    .totalChildren(calculationService.getTotalChildren(request.getRooms()))
                    .customerId(customerId)
                    .isLoggedIn(customerId != null)
                    .customerEmail(customerEmail)
                    .customerName(customerName)
                    .guestInfo(BookingResponseDTO.GuestBookingInfoDTO.builder()  // SỬA Ở ĐÂY
                            .fullName(guest.getFullName())
                            .email(guest.getEmail())
                            .phone(guest.getPhone())
                            .build())
                    .roomAllocations(allocations)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error creating booking", e);
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Lỗi khi đặt phòng: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}/check-in")
    public ResponseEntity<?> checkIn(@PathVariable Long id) {
        // 1. Tìm reservation
        Optional<Reservation> resOptional = reservationRepository.findById(id);
        if (resOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 2. Cập nhật status và thời gian
        Reservation reservation = resOptional.get();
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setActualCheckInDate(new Date());

        // 3. Cập nhật phòng
        List<ReservationRoom> rooms = reservationRoomRepository.findByReservationId(id);
        rooms.forEach(room -> room.setStatus(ReservationRoomStatus.CHECKED_IN));
        reservationRoomRepository.saveAll(rooms);

        // 4. Lưu và trả về
        reservationRepository.save(reservation);
        return ResponseEntity.ok(Map.of("message", "Check-in thành công"));
    }

    @PutMapping("/{id}/check-out")
    public ResponseEntity<?> checkOut(@PathVariable Long id) {
        Optional<Reservation> resOptional = reservationRepository.findById(id);
        if (resOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reservation reservation = resOptional.get();
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setActualCheckOutDate(new Date());

        // Cập nhật trạng thái các phòng
        List<ReservationRoom> rooms = reservationRoomRepository.findByReservationId(id);
        rooms.forEach(room -> room.setStatus(ReservationRoomStatus.CHECKED_OUT));
        reservationRoomRepository.saveAll(rooms);

        reservationRepository.save(reservation);

        try {
            taskClient.createCleaningTask(reservation.getId());
        } catch (Exception e) {
            log.error("Lỗi khi gửi yêu cầu dọn phòng", e);
        }

        String message = reservation.getCustomerId() != null
                ? "Check-out thành công. Cảm ơn khách hàng " + reservation.getCustomerId()
                : "Check-out thành công. Cảm ơn quý khách!";

        return ResponseEntity.ok(Map.of("message", message));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ReservationRoom> rooms = reservationRoomRepository.findByReservationId(id);
        List<Guest> guests = guestRepository.findByReservationId(id);

        Map<String, Object> response = new HashMap<>();
        response.put("reservation", reservation.get());
        response.put("rooms", rooms);
        response.put("guests", guests);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Reservation>> getBookingsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(reservationRepository.findByCustomerId(customerId));
    }
// Thêm vào BookingController.java

    /**
     * Lấy danh sách reservations chờ check-in
     * GET /api/checkin/reservations
     */
    @GetMapping("/checkin/reservations")
    public ResponseEntity<?> getPendingCheckIns() {
        log.info("Fetching pending check-in reservations");

        try {
            // Lấy reservations có status = PENDING và ngày check-in là hôm nay hoặc tương lai
            List<Reservation> pendingReservations = reservationRepository
                    .findByStatusAndExpectedCheckInDateGreaterThanEqual(
                            ReservationStatus.PENDING,
                            new Date()
                    );

            List<Map<String, Object>> result = new ArrayList<>();

            for (Reservation res : pendingReservations) {
                List<Guest> guests = guestRepository.findByReservationId(res.getId());
                Guest mainGuest = guests.isEmpty() ? null : guests.get(0);

                Map<String, Object> item = new HashMap<>();
                item.put("id", res.getId());
                item.put("reservationCode", res.getReservationCode());
                item.put("guestName", mainGuest != null ? mainGuest.getFullName() : "N/A");
                item.put("email", mainGuest != null ? mainGuest.getEmail() : "");
                item.put("phone", mainGuest != null ? mainGuest.getPhone() : "");
                item.put("arrivalDate", res.getExpectedCheckInDate());
                item.put("departureDate", res.getExpectedCheckOutDate());
                item.put("status", res.getStatus());

                result.add(item);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error fetching pending check-ins", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Lỗi tải danh sách: " + e.getMessage()
            ));
        }
    }

    /**
     * Check-in cho reservation (có receptionistId)
     * POST /api/checkin/reservation/{reservationId}
     */
    @PostMapping("/checkin/reservation/{reservationId}")
    public ResponseEntity<?> checkInReservation(
            @PathVariable Long reservationId,
            @RequestParam Long receptionistId) {

        log.info("Check-in reservation: {} by receptionist: {}", reservationId, receptionistId);

        try {
            // 1. Tìm reservation
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));

            // 2. Kiểm tra trạng thái
            if (reservation.getStatus() != ReservationStatus.PENDING) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Reservation đã được check-in hoặc hủy"
                ));
            }

            // 3. Cập nhật reservation
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.setActualCheckInDate(new Date());

            // 4. Cập nhật các phòng
            List<ReservationRoom> rooms = reservationRoomRepository
                    .findByReservationId(reservationId);

            for (ReservationRoom room : rooms) {
                room.setStatus(ReservationRoomStatus.CHECKED_IN);

                // Cập nhật trạng thái phòng trong Room Service
                try {
                    roomClient.updateRoomStatus(room.getRoomId(), "OCCUPIED");
                } catch (Exception e) {
                    log.error("Failed to update room status in Room Service", e);
                }
            }

            reservationRoomRepository.saveAll(rooms);
            reservationRepository.save(reservation);

            log.info("Check-in completed for reservation: {}", reservationId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Check-in thành công",
                    "reservationId", reservationId,
                    "receptionistId", receptionistId
            ));

        } catch (Exception e) {
            log.error("Check-in error", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Lỗi check-in: " + e.getMessage()
            ));
        }
    }
}