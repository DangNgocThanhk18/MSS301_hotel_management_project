// Tạo file mới: WalkInCheckInController.java
package com.mss301.bookingservice.controllers;

import com.mss301.bookingservice.client.RoomClient;
import com.mss301.bookingservice.dto.*;
import com.mss301.bookingservice.enums.ReservationRoomStatus;
import com.mss301.bookingservice.enums.ReservationStatus;
import com.mss301.bookingservice.pojos.Guest;
import com.mss301.bookingservice.pojos.Reservation;
import com.mss301.bookingservice.pojos.ReservationRoom;
import com.mss301.bookingservice.repository.GuestRepository;
import com.mss301.bookingservice.repository.ReservationRepository;
import com.mss301.bookingservice.repository.ReservationRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/checkin/walkin")
@RequiredArgsConstructor
@Slf4j
public class WalkInCheckInController {

    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final GuestRepository guestRepository;
    private final RoomClient roomClient;

    @PostConstruct
    public void init() {
        log.info("==================================================");
        log.info("✅✅✅ WalkInCheckInController INITIALIZED SUCCESSFULLY ✅✅✅");
        log.info("Base path: /api/checkin/walkin");
        log.info("Endpoints:");
        log.info("  - GET  /today");
        log.info("  - POST /checkin");
        log.info("  - GET  /available-rooms");
        log.info("  - GET  /calculate-price");
        log.info("==================================================");
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        log.info("✅ Test endpoint called");
        return ResponseEntity.ok(Map.of(
                "message", "WalkInCheckInController is working!",
                "timestamp", new Date().toString(),
                "status", "OK"
        ));
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayWalkIns() {
        log.info("========== GET /api/checkin/walkin/today ==========");

        try {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date startOfDay = cal.getTime();

            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date endOfDay = cal.getTime();

            log.info("StartOfDay: {}, EndOfDay: {}", startOfDay, endOfDay);

            List<Reservation> walkIns = reservationRepository
                    .findByReservationCodeStartingWithAndCreatedDateBetween(
                            "WALKIN-", startOfDay, endOfDay);

            log.info("Found {} walk-ins today", walkIns.size());

            List<Map<String, Object>> result = new ArrayList<>();

            for (Reservation res : walkIns) {
                List<ReservationRoom> rooms = reservationRoomRepository.findByReservationId(res.getId());
                List<Guest> guests = guestRepository.findByReservationId(res.getId());

                for (ReservationRoom room : rooms) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("reservationId", res.getId());
                    item.put("reservationCode", res.getReservationCode());
                    item.put("guestName", guests.isEmpty() ? "N/A" : guests.get(0).getFullName());
                    item.put("roomId", room.getRoomId());
                    item.put("roomNumber", "P" + room.getRoomId());
                    item.put("checkInDate", res.getActualCheckInDate());
                    item.put("checkOutDate", res.getExpectedCheckOutDate());
                    item.put("status", room.getStatus());
                    result.add(item);
                }
            }

            log.info("Returning {} items", result.size());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error getting today's walk-ins", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Lỗi lấy danh sách: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/checkin")
    public ResponseEntity<?> walkInCheckIn(@RequestBody WalkInCheckInDTO request) {
        log.info("========== WALK-IN CHECK-IN ==========");
        log.info("Guest name: {}", request.getGuestName());
        log.info("Room type ID: {}", request.getRoomTypeId());
        log.info("Check-in: {}, Check-out: {}", request.getCheckInDate(), request.getCheckOutDate());

        try {
            // 1. Lấy thông tin room type
            RoomTypeDTO roomType = roomClient.getRoomTypeById(request.getRoomTypeId());
            log.info("Room type found: {}", roomType.getName());

            // 2. Kiểm tra phòng trống
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String checkInStr = dateFormat.format(request.getCheckInDate());
            String checkOutStr = dateFormat.format(request.getCheckOutDate());

            List<Long> availableRooms = roomClient.findAvailableRooms(
                    request.getRoomTypeId(), checkInStr, checkOutStr, 1);

            if (availableRooms.isEmpty()) {
                log.warn("No available rooms for room type: {}", request.getRoomTypeId());
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Không còn phòng trống cho loại phòng này"
                ));
            }

            Long selectedRoomId = availableRooms.get(0);
            log.info("Selected room ID: {}", selectedRoomId);

            // 3. Tính tiền
            long nights = calculateNights(request.getCheckInDate(), request.getCheckOutDate());
            BigDecimal totalAmountUSD = roomType.getBasePrice()
                    .multiply(BigDecimal.valueOf(nights));
            BigDecimal totalAmountVND = totalAmountUSD.multiply(BigDecimal.valueOf(23000));

            log.info("Nights: {}, Total amount: {} VND", nights, totalAmountVND);

            // 4. Tạo Reservation
            Reservation reservation = new Reservation();
            reservation.setReservationCode("WALKIN-" + System.currentTimeMillis());
            reservation.setCustomerId(null);
            reservation.setHotelId(1L);
            reservation.setExpectedCheckInDate(request.getCheckInDate());
            reservation.setExpectedCheckOutDate(request.getCheckOutDate());
            reservation.setActualCheckInDate(new Date());
            reservation.setNote(request.getNotes());
            reservation.setTotalAmount(totalAmountVND);
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.setCreatedDate(new Date());

            reservation = reservationRepository.save(reservation);
            log.info("Reservation saved with ID: {}", reservation.getId());

            // 5. Tạo Guest
            Guest guest = new Guest();
            guest.setFullName(request.getGuestName());
            guest.setEmail(request.getEmail() != null ? request.getEmail() : "");
            guest.setPhone(request.getPhone() != null ? request.getPhone() : "");
            guest.setNationality(request.getNationality());
            guest.setDocumentType(request.getDocumentType());
            guest.setDocumentNumber(request.getDocumentNumber());
            guest.setReservation(reservation);

            guestRepository.save(guest);
            log.info("Guest saved: {}", guest.getFullName());

            // 6. Tạo ReservationRoom và check-in luôn
            ReservationRoom reservationRoom = new ReservationRoom();
            reservationRoom.setReservation(reservation);
            reservationRoom.setRoomId(selectedRoomId);
            reservationRoom.setRoomTypeId(request.getRoomTypeId());
            reservationRoom.setNightlyPrice(roomType.getBasePrice());
            reservationRoom.setStatus(ReservationRoomStatus.CHECKED_IN);
            reservationRoom.setAdultCount(request.getAdultCount());
            reservationRoom.setChildCount(request.getChildCount() != null ? request.getChildCount() : 0);

            reservationRoomRepository.save(reservationRoom);
            log.info("Reservation room saved: roomId={}, status={}", selectedRoomId, reservationRoom.getStatus());

            // 7. Cập nhật trạng thái phòng trong Room Service
            try {
                roomClient.updateRoomStatus(selectedRoomId, "OCCUPIED");
                log.info("Updated room {} status to OCCUPIED", selectedRoomId);
            } catch (Exception e) {
                log.error("Failed to update room status in Room Service", e);
            }

            // 8. Tạo response
            WalkInCheckInResponseDTO response = WalkInCheckInResponseDTO.builder()
                    .success(true)
                    .message("Walk-in check-in thành công")
                    .reservationId(reservation.getId())
                    .reservationCode(reservation.getReservationCode())
                    .roomId(selectedRoomId)
                    .roomNumber("P" + selectedRoomId)
                    .roomType(roomType.getName())
                    .guestName(request.getGuestName())
                    .checkInDate(request.getCheckInDate())
                    .checkOutDate(request.getCheckOutDate())
                    .totalAmount(totalAmountVND)
                    .deposit(totalAmountVND.multiply(new BigDecimal("0.2")))
                    .build();

            log.info("Walk-in check-in completed successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Walk-in check-in error", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Lỗi check-in: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<?> getAvailableRooms(
            @RequestParam("roomTypeId") Long roomTypeId,
            @RequestParam("checkIn") @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkIn,
            @RequestParam("checkOut") @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkOut) {

        log.info("========== GET AVAILABLE ROOMS ==========");
        log.info("RoomTypeId: {}, CheckIn: {}, CheckOut: {}", roomTypeId, checkIn, checkOut);

        try {
            // Format date cho Room Service
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String checkInStr = dateFormat.format(checkIn);
            String checkOutStr = dateFormat.format(checkOut);

            // Gọi Room Service để tìm phòng trống
            List<Long> availableRoomIds = roomClient.findAvailableRooms(
                    roomTypeId, checkInStr, checkOutStr, 10); // Lấy tối đa 10 phòng

            log.info("Found {} available rooms", availableRoomIds.size());

            List<Map<String, Object>> result = new ArrayList<>();

            for (Long roomId : availableRoomIds) {
                Map<String, Object> roomInfo = new HashMap<>();
                roomInfo.put("roomId", roomId);
                roomInfo.put("roomNumber", "P" + roomId);
                roomInfo.put("status", "AVAILABLE");
                result.add(roomInfo);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error finding available rooms", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Lỗi tìm phòng: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/calculate-price")
    public ResponseEntity<?> calculatePrice(
            @RequestParam("roomTypeId") Long roomTypeId,
            @RequestParam("checkIn") @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkIn,
            @RequestParam("checkOut") @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkOut) {

        log.info("========== CALCULATE PRICE ==========");
        log.info("RoomTypeId: {}, CheckIn: {}, CheckOut: {}", roomTypeId, checkIn, checkOut);

        try {
            // 1. Lấy thông tin room type từ Room Service
            RoomTypeDTO roomType = roomClient.getRoomTypeById(roomTypeId);

            // 2. Tính số đêm
            long nights = calculateNights(checkIn, checkOut);

            // 3. Tính giá (USD -> VND)
            BigDecimal totalPriceUSD = roomType.getBasePrice()
                    .multiply(BigDecimal.valueOf(nights));

            BigDecimal totalPriceVND = totalPriceUSD
                    .multiply(BigDecimal.valueOf(23000));

            // 4. Tính tiền cọc 20%
            BigDecimal deposit = totalPriceVND
                    .multiply(new BigDecimal("0.2"));

            // 5. Tính giá mỗi đêm
            BigDecimal pricePerNightVND = roomType.getBasePrice()
                    .multiply(BigDecimal.valueOf(23000));

            log.info("Nights: {}, Total: {} VND, Deposit: {} VND",
                    nights, totalPriceVND, deposit);

            // 6. Trả về kết quả
            return ResponseEntity.ok(Map.of(
                    "totalPrice", totalPriceVND,
                    "deposit", deposit,
                    "nights", nights,
                    "pricePerNight", pricePerNightVND,
                    "roomTypeName", roomType.getName(),
                    "capacity", roomType.getCapacity()
            ));

        } catch (Exception e) {
            log.error("Error calculating price", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Lỗi tính giá: " + e.getMessage()
            ));
        }
    }

    private long calculateNights(Date checkIn, Date checkOut) {
        long diffInMillies = checkOut.getTime() - checkIn.getTime();
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}