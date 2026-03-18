package com.mss301.paymentservice.controllers;

import com.mss301.paymentservice.dto.PaymentRequestDTO;
import com.mss301.paymentservice.dto.PaymentResponseDTO;
import com.mss301.paymentservice.enums.PaymentMethod;
import com.mss301.paymentservice.enums.PaymentStatus;
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
    /**
     * Xử lý thanh toán khi checkout
     * POST /api/payments/process-checkout
     */
    @PostMapping("/process-checkout")
    public ResponseEntity<?> processCheckoutPayment(@RequestBody Map<String, Object> request) {
        try {
            Long reservationId = Long.valueOf(request.get("reservationId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String paymentMethod = request.get("paymentMethod").toString();
            String description = request.getOrDefault("description", "Thanh toán khi checkout").toString();

            log.info("Processing checkout payment for reservation: {}, amount: {}, method: {}",
                    reservationId, amount, paymentMethod);

            // Tạo payment request
            PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
            paymentRequest.setReservationId(reservationId);
            paymentRequest.setAmount(amount);

            // Chuyển đổi payment method string sang enum
            switch (paymentMethod.toUpperCase()) {
                case "CASH":
                    paymentRequest.setPaymentMethod(PaymentMethod.CASH);
                    break;
                case "BANK_TRANSFER":
                    paymentRequest.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
                    break;
                case "CREDIT_CARD":
                    paymentRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);
                    break;
                default:
                    paymentRequest.setPaymentMethod(PaymentMethod.CASH);
            }

            // Tạo payment (không cần HttpServletRequest vì không phải VNPay)
            PaymentResponseDTO response = paymentService.createPayment(paymentRequest, null);

            log.info("Checkout payment created with ID: {}", response.getId());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Thanh toán thành công",
                    "paymentId", response.getId(),
                    "amount", response.getAmount(),
                    "reservationId", reservationId
            ));

        } catch (Exception e) {
            log.error("Error processing checkout payment", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Lỗi thanh toán: " + e.getMessage()
            ));
        }
    }

    /**
     * Tính tổng số tiền đã thanh toán của một reservation
     * GET /api/payments/total-paid/{reservationId}
     */
    @GetMapping("/total-paid/{reservationId}")
    public ResponseEntity<?> getTotalPaidByReservation(@PathVariable Long reservationId) {
        try {
            List<PaymentResponseDTO> payments = paymentService.getPaymentsByReservation(reservationId);

            BigDecimal totalPaid = payments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                    .map(PaymentResponseDTO::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return ResponseEntity.ok(Map.of(
                    "reservationId", reservationId,
                    "totalPaid", totalPaid,
                    "paymentCount", payments.size()
            ));
        } catch (Exception e) {
            log.error("Error calculating total paid", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}