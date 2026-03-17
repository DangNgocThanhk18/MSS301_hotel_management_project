// src/main/java/com/mss301/paymentservice/controllers/PaymentController.java
package com.mss301.paymentservice.controllers;

import com.mss301.paymentservice.dto.PaymentRequestDTO;
import com.mss301.paymentservice.dto.PaymentResponseDTO;
import com.mss301.paymentservice.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @Valid @RequestBody PaymentRequestDTO request,
            HttpServletRequest httpRequest) {
        try {
            PaymentResponseDTO response = paymentService.createPayment(request, httpRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating payment", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/create-vnpay")
    public ResponseEntity<?> createVnPayPayment(
            @RequestBody Map<String, Long> request,
            HttpServletRequest httpRequest) {
        try {
            Long reservationId = request.get("reservationId");
            if (reservationId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Missing reservationId"
                ));
            }

            // Tạm thời hardcode amount - trong thực tế cần lấy từ reservation
            PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
            paymentRequest.setReservationId(reservationId);
            paymentRequest.setAmount(new java.math.BigDecimal("1000000")); // Hardcode
            paymentRequest.setPaymentMethod(com.mss301.paymentservice.enums.PaymentMethod.VNPAY);

            PaymentResponseDTO response = paymentService.createPayment(paymentRequest, httpRequest);

            return ResponseEntity.ok(Map.of(
                    "vnpayUrl", response.getPaymentUrl()
            ));
        } catch (Exception e) {
            log.error("Error creating VNPay payment", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnPayReturn(@RequestParam Map<String, String> params) {
        log.info("VNPay return received with params: {}", params);

        Map<String, Object> result = paymentService.processVnPayReturn(params);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<?> getPaymentsByReservation(@PathVariable Long reservationId) {
        try {
            List<PaymentResponseDTO> payments = paymentService.getPaymentsByReservation(reservationId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        try {
            PaymentResponseDTO payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{reservationId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable Long reservationId) {
        try {
            Map<String, Object> status = paymentService.getPaymentStatus(reservationId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
