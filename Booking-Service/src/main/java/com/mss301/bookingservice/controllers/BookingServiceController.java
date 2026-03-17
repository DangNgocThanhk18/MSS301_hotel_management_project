// src/main/java/com/mss301/bookingservice/controllers/BookingServiceController.java
package com.mss301.bookingservice.controllers;

import com.mss301.bookingservice.client.ServiceClient;
import com.mss301.bookingservice.dto.BookingServiceRequestDTO;
import com.mss301.bookingservice.dto.BookingServiceResponseDTO;
import com.mss301.bookingservice.dto.ServiceDTO;
import com.mss301.bookingservice.enums.ReservationServiceStatus;
import com.mss301.bookingservice.pojos.Reservation;
import com.mss301.bookingservice.pojos.ReservationService;
import com.mss301.bookingservice.repository.ReservationRepository;
import com.mss301.bookingservice.repository.ReservationServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings/services")
@RequiredArgsConstructor
@Slf4j
public class BookingServiceController {

    private final ReservationServiceRepository reservationServiceRepository;
    private final ReservationRepository reservationRepository;
    private final ServiceClient serviceClient;

    @PostMapping
    public ResponseEntity<?> addServicesToBooking(@RequestBody BookingServiceRequestDTO request) {
        log.info("Adding services to reservation: {}", request.getReservationId());

        try {
            Reservation reservation = reservationRepository.findById(request.getReservationId())
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));

            List<ReservationService> savedServices = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (BookingServiceRequestDTO.ServiceItem item : request.getServices()) {
                // Lấy thông tin service từ Hotel Service
                ServiceDTO service = serviceClient.getServiceById(item.getServiceId());

                ReservationService reservationService = new ReservationService();
                reservationService.setReservation(reservation);
                reservationService.setServiceId(item.getServiceId());
                reservationService.setQuantity(item.getQuantity());

                BigDecimal itemTotal = service.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                reservationService.setTotalPrice(itemTotal);
                reservationService.setStatus(ReservationServiceStatus.PENDING);

                savedServices.add(reservationServiceRepository.save(reservationService));
                totalAmount = totalAmount.add(itemTotal);
            }

            // Cập nhật tổng tiền của reservation (nếu cần)
            reservation.setTotalAmount(reservation.getTotalAmount().add(totalAmount));
            reservationRepository.save(reservation);

            List<BookingServiceResponseDTO.ServiceItemDTO> itemDTOs = savedServices.stream()
                    .map(rs -> {
                        ServiceDTO service = serviceClient.getServiceById(rs.getServiceId());
                        return BookingServiceResponseDTO.ServiceItemDTO.builder()
                                .serviceId(rs.getServiceId())
                                .serviceName(service.getName())
                                .quantity(rs.getQuantity())
                                .unitPrice(service.getPrice())
                                .totalPrice(rs.getTotalPrice())
                                .build();
                    })
                    .collect(Collectors.toList());

            BookingServiceResponseDTO response = BookingServiceResponseDTO.builder()
                    .reservationId(reservation.getId())
                    .services(itemDTOs)
                    .totalAmount(totalAmount)
                    .status("SUCCESS")
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error adding services", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<?> getServicesByReservation(@PathVariable Long reservationId) {
        List<ReservationService> services = reservationServiceRepository.findByReservationId(reservationId);

        List<BookingServiceResponseDTO.ServiceItemDTO> itemDTOs = services.stream()
                .map(rs -> {
                    try {
                        ServiceDTO service = serviceClient.getServiceById(rs.getServiceId());
                        return BookingServiceResponseDTO.ServiceItemDTO.builder()
                                .serviceId(rs.getServiceId())
                                .serviceName(service.getName())
                                .quantity(rs.getQuantity())
                                .unitPrice(service.getPrice())
                                .totalPrice(rs.getTotalPrice())
                                .build();
                    } catch (Exception e) {
                        return BookingServiceResponseDTO.ServiceItemDTO.builder()
                                .serviceId(rs.getServiceId())
                                .serviceName("Unknown Service")
                                .quantity(rs.getQuantity())
                                .totalPrice(rs.getTotalPrice())
                                .build();
                    }
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(itemDTOs);
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> removeService(@PathVariable Long serviceId) {
        try {
            reservationServiceRepository.deleteById(serviceId);
            return ResponseEntity.ok(Map.of("message", "Service removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
