// src/main/java/com/mss301/bookingservice/controllers/BookingController.java
package com.mss301.bookingservice.controllers;

import com.mss301.bookingservice.client.PaymentClient;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

    // Sửa method getBookingsByCustomer trong BookingController.java
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getBookingsByCustomer(@PathVariable Long customerId) {
        log.info("Fetching bookings for customer: {}", customerId);

        try {
            // Kiểm tra customerId hợp lệ
            if (customerId == null || customerId <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Mã khách hàng không hợp lệ"
                ));
            }

            List<Reservation> reservations = reservationRepository.findByCustomerId(customerId);

            if (reservations.isEmpty()) {
                log.info("No bookings found for customer: {}", customerId);
                return ResponseEntity.ok(Collections.emptyList());
            }

            // Thêm thông tin số phòng cho mỗi reservation
            List<Map<String, Object>> result = new ArrayList<>();
            for (Reservation res : reservations) {
                List<ReservationRoom> rooms = reservationRoomRepository.findByReservationId(res.getId());

                Map<String, Object> item = new HashMap<>();
                item.put("id", res.getId());
                item.put("reservationCode", res.getReservationCode());
                item.put("hotelId", res.getHotelId());
                item.put("expectedCheckInDate", res.getExpectedCheckInDate());
                item.put("expectedCheckOutDate", res.getExpectedCheckOutDate());
                item.put("totalAmount", res.getTotalAmount());
                item.put("status", res.getStatus());
                item.put("createdDate", res.getCreatedDate());
                item.put("roomCount", rooms.size());

                result.add(item);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error fetching bookings for customer: {}", customerId, e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Lỗi khi tải danh sách đặt phòng: " + e.getMessage()
            ));
        }
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
    // Thêm vào cuối file BookingController.java, trước dấu } cuối cùng

    /**
     * Lấy danh sách khách đang ở trong khách sạn (đã check-in và chưa check-out)
     * GET /api/checkin/current-guests
     */
    @GetMapping("/checkin/current-guests")
    public ResponseEntity<?> getCurrentGuests() {
        log.info("Fetching current guests (checked-in) in hotel");

        try {
            // Lấy tất cả reservations có status = CONFIRMED (đã check-in)
            List<Reservation> currentReservations = reservationRepository
                    .findByStatus(ReservationStatus.CONFIRMED);

            List<Map<String, Object>> result = new ArrayList<>();

            for (Reservation res : currentReservations) {
                List<Guest> guests = guestRepository.findByReservationId(res.getId());
                Guest mainGuest = guests.isEmpty() ? null : guests.get(0);

                // Lấy thông tin các phòng của reservation này
                List<ReservationRoom> rooms = reservationRoomRepository
                        .findByReservationId(res.getId());

                for (ReservationRoom room : rooms) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("reservationId", res.getId());
                    item.put("reservationCode", res.getReservationCode());
                    item.put("guestName", mainGuest != null ? mainGuest.getFullName() : "N/A");
                    item.put("email", mainGuest != null ? mainGuest.getEmail() : "");
                    item.put("phone", mainGuest != null ? mainGuest.getPhone() : "");
                    item.put("roomId", room.getRoomId());
                    item.put("roomNumber", "P" + room.getRoomId()); // Format phòng
                    item.put("roomTypeId", room.getRoomTypeId());
                    item.put("checkInDate", res.getActualCheckInDate() != null
                            ? res.getActualCheckInDate() : res.getExpectedCheckInDate());
                    item.put("expectedCheckOutDate", res.getExpectedCheckOutDate());
                    item.put("status", room.getStatus()); // CHECKED_IN

                    result.add(item);
                }
            }

            log.info("Found {} current guests", result.size());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error fetching current guests", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Lỗi tải danh sách khách đang ở: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy danh sách khách đã check-in hôm nay
     * GET /api/checkin/today
     */
    @GetMapping("/checkin/today")
    public ResponseEntity<?> getTodayCheckIns() {
        log.info("Fetching today's check-ins");

        try {
            // Lấy đầu ngày hôm nay
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date startOfDay = cal.getTime();

            // Lấy cuối ngày hôm nay
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date endOfDay = cal.getTime();

            // Lấy tất cả reservations đã check-in hôm nay
            List<Reservation> todayCheckIns = reservationRepository
                    .findByActualCheckInDateBetween(startOfDay, endOfDay);

            List<Map<String, Object>> result = new ArrayList<>();

            for (Reservation res : todayCheckIns) {
                List<Guest> guests = guestRepository.findByReservationId(res.getId());
                Guest mainGuest = guests.isEmpty() ? null : guests.get(0);

                List<ReservationRoom> rooms = reservationRoomRepository
                        .findByReservationId(res.getId());

                for (ReservationRoom room : rooms) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("reservationId", res.getId());
                    item.put("reservationCode", res.getReservationCode());
                    item.put("guestName", mainGuest != null ? mainGuest.getFullName() : "N/A");
                    item.put("email", mainGuest != null ? mainGuest.getEmail() : "");
                    item.put("phone", mainGuest != null ? mainGuest.getPhone() : "");
                    item.put("roomId", room.getRoomId());
                    item.put("roomNumber", "P" + room.getRoomId());
                    item.put("roomTypeId", room.getRoomTypeId());
                    item.put("checkInDate", res.getActualCheckInDate());
                    item.put("expectedCheckOutDate", res.getExpectedCheckOutDate());
                    item.put("status", room.getStatus());

                    result.add(item);
                }
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error fetching today's check-ins", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Lỗi tải danh sách check-in hôm nay: " + e.getMessage()
            ));
        }
    }
    // Thêm vào BookingController.java

    @Autowired
    private PaymentClient paymentClient;

    /**
     * Check-out với thanh toán số tiền còn lại
     * POST /api/bookings/{id}/checkout-with-payment
     */
    @PostMapping("/{id}/checkout-with-payment")
    public ResponseEntity<?> checkoutWithPayment(
            @PathVariable Long id,
            @RequestBody CheckoutRequestDTO request) {

        log.info("Processing checkout for reservation: {} with payment", id);

        try {
            // 1. Tìm reservation
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));

            // 2. Kiểm tra trạng thái
            if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Reservation không ở trạng thái check-in"
                ));
            }

            // 3. Lấy thông tin khách và phòng
            List<Guest> guests = guestRepository.findByReservationId(id);
            Guest mainGuest = guests.isEmpty() ? null : guests.get(0);

            List<ReservationRoom> rooms = reservationRoomRepository.findByReservationId(id);
            if (rooms.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Không tìm thấy thông tin phòng"
                ));
            }

            // 4. Tính số ngày ở thực tế
            Date actualCheckIn = reservation.getActualCheckInDate();
            Date now = new Date();
            long nights = calculateNights(actualCheckIn, now);

            // 5. Tính tổng tiền dựa trên số ngày ở thực tế
            BigDecimal nightlyPrice = rooms.get(0).getNightlyPrice();
            BigDecimal totalActualAmount = nightlyPrice
                    .multiply(BigDecimal.valueOf(nights))
                    .multiply(BigDecimal.valueOf(rooms.size()))
                    .multiply(BigDecimal.valueOf(23000)); // USD to VND

            // 6. Xác định loại khách hàng và số tiền cần thanh toán
            BigDecimal paidAmount = BigDecimal.ZERO;
            BigDecimal remainingAmount = BigDecimal.ZERO;
            String customerType = "WALK_IN";

            // Lấy thông tin thanh toán từ Payment Service
            try {
                Map<String, Object> paymentInfo = paymentClient.getPaymentsByReservation(id);
                if (paymentInfo != null && paymentInfo.containsKey("totalPaid")) {
                    paidAmount = new BigDecimal(paymentInfo.get("totalPaid").toString());
                    customerType = "REGISTERED";
                }
            } catch (Exception e) {
                log.warn("Could not fetch payment info from Payment Service", e);
            }

            // Tính số tiền còn lại cần thanh toán
            if (customerType.equals("REGISTERED") && paidAmount.compareTo(BigDecimal.ZERO) > 0) {
                // Khách đã đặt online: đã trả 20% cọc, cần thanh toán 80% còn lại
                remainingAmount = totalActualAmount.subtract(paidAmount);
            } else {
                // Khách vãng lai: chưa thanh toán gì, cần thanh toán 100%
                remainingAmount = totalActualAmount;
            }

            // 7. Xử lý thanh toán
            BigDecimal finalAmount = BigDecimal.ZERO;
            if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
                // Gọi Payment Service để thanh toán số tiền còn lại
                Map<String, Object> paymentRequest = new HashMap<>();
                paymentRequest.put("reservationId", id);
                paymentRequest.put("amount", remainingAmount);
                paymentRequest.put("paymentMethod", request.getPaymentMethod());
                paymentRequest.put("description", "Thanh toán khi checkout - " +
                        (customerType.equals("REGISTERED") ? "80% còn lại" : "100%"));

                Map<String, Object> paymentResult = paymentClient.processCheckoutPayment(paymentRequest);

                if (paymentResult != null && "success".equals(paymentResult.get("status"))) {
                    finalAmount = new BigDecimal(paymentResult.get("amount").toString());
                    log.info("Payment processed successfully: {}", finalAmount);
                } else {
                    throw new RuntimeException("Thanh toán thất bại");
                }
            }

            // 8. Cập nhật reservation
            reservation.setStatus(ReservationStatus.COMPLETED);
            reservation.setActualCheckOutDate(now);

            // 9. Cập nhật các phòng
            for (ReservationRoom room : rooms) {
                room.setStatus(ReservationRoomStatus.CHECKED_OUT);

                // Cập nhật trạng thái phòng trong Room Service
                try {
                    roomClient.updateRoomStatus(room.getRoomId(), "AVAILABLE");
                } catch (Exception e) {
                    log.error("Failed to update room status in Room Service", e);
                }
            }

            reservationRoomRepository.saveAll(rooms);
            reservationRepository.save(reservation);

            // 10. Gửi yêu cầu dọn phòng
            try {
                taskClient.createCleaningTask(reservation.getId());
            } catch (Exception e) {
                log.error("Lỗi khi gửi yêu cầu dọn phòng", e);
            }

            // 11. Tạo response
            CheckoutResponseDTO response = CheckoutResponseDTO.builder()
                    .success(true)
                    .reservationId(reservation.getId())
                    .reservationCode(reservation.getReservationCode())
                    .guestName(mainGuest != null ? mainGuest.getFullName() : "N/A")
                    .roomId(rooms.get(0).getRoomId())
                    .roomNumber("P" + rooms.get(0).getRoomId())
                    .checkInDate(actualCheckIn)
                    .checkOutDate(now)
                    .stayNights(nights)
                    .totalAmount(totalActualAmount)
                    .paidAmount(paidAmount)
                    .remainingAmount(remainingAmount)
                    .finalAmount(finalAmount)
                    .customerType(customerType)
                    .message(customerType.equals("REGISTERED")
                            ? String.format("Đã thanh toán %s VNĐ (80%% còn lại)", finalAmount)
                            : String.format("Đã thanh toán %s VNĐ (100%%)", finalAmount))
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Checkout error", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Lỗi checkout: " + e.getMessage()
            ));
        }
    }
    private long calculateNights(Date checkIn, Date checkOut) {
        long diffInMillies = checkOut.getTime() - checkIn.getTime();
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}