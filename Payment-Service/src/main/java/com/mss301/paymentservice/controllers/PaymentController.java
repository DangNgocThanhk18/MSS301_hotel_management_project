package com.mss301.paymentservice.controllers;

import com.mss301.paymentservice.dto.PaymentRequestDTO;
import com.mss301.paymentservice.dto.PaymentResponseDTO;
import com.mss301.paymentservice.enums.PaymentMethod;
import com.mss301.paymentservice.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    // ⚠️ QUAN TRỌNG: Đặt các endpoint CỤ THỂ trước endpoint CÓ BIẾN ĐỘNG ({id})

    /**
     * Tạo thanh toán VNPay - ĐẶT TRƯỚC
     * POST /api/payments/create-vnpay
     */
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

            log.info("Creating VNPay payment for reservation: {}", reservationId);

            // Tạo payment request với số tiền mặc định (sẽ lấy từ reservation trong thực tế)
            PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
            paymentRequest.setReservationId(reservationId);
            paymentRequest.setAmount(new BigDecimal("1000000")); // Hardcode, cần lấy từ reservation
            paymentRequest.setPaymentMethod(PaymentMethod.VNPAY);

            PaymentResponseDTO response = paymentService.createPayment(paymentRequest, httpRequest);

            log.info("VNPay payment created with ID: {}, URL: {}", response.getId(), response.getPaymentUrl());

            return ResponseEntity.ok(Map.of(
                    "vnpayUrl", response.getPaymentUrl(),
                    "paymentId", response.getId(),
                    "amount", response.getAmount()
            ));

        } catch (Exception e) {
            log.error("Error creating VNPay payment", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Tạo thanh toán thông thường
     * POST /api/payments/create
     */
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

    /**
     * Xử lý return từ VNPay
     * GET /api/payments/vnpay-return
     */
    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnPayReturn(@RequestParam Map<String, String> params) {
        log.info("VNPay return received with params: {}", params.keySet());

        Map<String, Object> result = paymentService.processVnPayReturn(params);
        return ResponseEntity.ok(result);
    }

    /**
     * Lấy payment theo ID - ĐẶT SAU CÙNG
     * GET /api/payments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        try {
            PaymentResponseDTO payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Lấy payments theo reservation
     * GET /api/payments/reservation/{reservationId}
     */
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<?> getPaymentsByReservation(@PathVariable Long reservationId) {
        try {
            List<PaymentResponseDTO> payments = paymentService.getPaymentsByReservation(reservationId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Lấy trạng thái thanh toán của reservation
     * GET /api/payments/status/{reservationId}
     */
    @GetMapping("/status/{reservationId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable Long reservationId) {
        try {
            Map<String, Object> status = paymentService.getPaymentStatus(reservationId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Test endpoint
     * GET /api/payments/test
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Payment service is running",
                "port", 8008,
                "timestamp", System.currentTimeMillis()
        ));
    }
}