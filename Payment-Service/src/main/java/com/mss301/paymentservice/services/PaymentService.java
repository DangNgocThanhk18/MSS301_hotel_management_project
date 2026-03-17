// src/main/java/com/mss301/paymentservice/services/PaymentService.java
package com.mss301.paymentservice.services;

import com.mss301.paymentservice.dto.PaymentRequestDTO;
import com.mss301.paymentservice.dto.PaymentResponseDTO;
import com.mss301.paymentservice.enums.InvoiceStatus;
import com.mss301.paymentservice.enums.PaymentMethod;
import com.mss301.paymentservice.enums.PaymentStatus;
import com.mss301.paymentservice.pojos.Invoice;
import com.mss301.paymentservice.pojos.Payment;
import com.mss301.paymentservice.repositories.InvoiceRepository;
import com.mss301.paymentservice.repositories.PaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final VnPayService vnPayService;

    public PaymentResponseDTO createPayment(PaymentRequestDTO request, HttpServletRequest httpRequest) {
        log.info("Creating payment for reservation: {}", request.getReservationId());

        Payment payment = Payment.builder()
                .reservationId(request.getReservationId())
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "VND")
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment created with ID: {}", payment.getId());

        // Nếu là VNPay, tạo payment URL
        String paymentUrl = null;
        if (request.getPaymentMethod() == PaymentMethod.VNPAY) {
            paymentUrl = vnPayService.generatePaymentUrl(payment, httpRequest);
        }

        return mapToResponseDTO(payment, paymentUrl);
    }

    @Transactional
    public Map<String, Object> processVnPayReturn(Map<String, String> params) {
        log.info("Processing VNPay return");

        // Validate signature
        if (!vnPayService.validateVnPayReturn(params)) {
            log.error("Invalid VNPay signature");
            return Map.of(
                    "status", "error",
                    "message", "Invalid signature"
            );
        }

        String vnp_TxnRef = params.get("vnp_TxnRef");
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TransactionNo = params.get("vnp_TransactionNo");
        String vnp_Amount = params.get("vnp_Amount");

        try {
            Long paymentId = Long.valueOf(vnp_TxnRef);
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

            // Cập nhật payment
            payment.setTransactionRef(vnp_TransactionNo);

            if ("00".equals(vnp_ResponseCode)) {
                // Thanh toán thành công
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaidAt(LocalDateTime.now());

                // Tạo invoice
                createInvoice(payment);

                log.info("Payment completed successfully: {}", paymentId);

                return Map.of(
                        "status", "success",
                        "message", "Thanh toán thành công",
                        "reservationId", payment.getReservationId(),
                        "paymentId", payment.getId(),
                        "transactionRef", vnp_TransactionNo
                );
            } else {
                // Thanh toán thất bại
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);

                log.warn("Payment failed: {}, code: {}", paymentId, vnp_ResponseCode);

                return Map.of(
                        "status", "failed",
                        "message", "Thanh toán thất bại",
                        "reservationId", payment.getReservationId(),
                        "code", vnp_ResponseCode
                );
            }

        } catch (Exception e) {
            log.error("Error processing VNPay return", e);
            return Map.of(
                    "status", "error",
                    "message", e.getMessage()
            );
        }
    }

    private void createInvoice(Payment payment) {
        Invoice invoice = Invoice.builder()
                .reservationId(payment.getReservationId())
                .invoiceNumber("INV-" + System.currentTimeMillis())
                .total(payment.getAmount())
                .tax(BigDecimal.ZERO)
                .status(InvoiceStatus.PAID)
                .issuedAt(LocalDateTime.now())
                .build();

        invoiceRepository.save(invoice);
        log.info("Invoice created for payment: {}", payment.getId());
    }

    public List<PaymentResponseDTO> getPaymentsByReservation(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
        return mapToResponseDTO(payment);
    }

    public Map<String, Object> getPaymentStatus(Long reservationId) {
        List<Payment> payments = paymentRepository.findByReservationId(reservationId);

        BigDecimal totalPaid = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean hasSuccessfulPayment = payments.stream()
                .anyMatch(p -> p.getStatus() == PaymentStatus.COMPLETED);

        Map<String, Object> status = new HashMap<>();
        status.put("reservationId", reservationId);
        status.put("totalPaid", totalPaid);
        status.put("hasSuccessfulPayment", hasSuccessfulPayment);
        status.put("payments", payments.stream().map(this::mapToResponseDTO).collect(Collectors.toList()));

        return status;
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment) {
        return mapToResponseDTO(payment, null);
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment, String paymentUrl) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .reservationId(payment.getReservationId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionRef(payment.getTransactionRef())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .paymentUrl(paymentUrl)
                .build();
    }
}