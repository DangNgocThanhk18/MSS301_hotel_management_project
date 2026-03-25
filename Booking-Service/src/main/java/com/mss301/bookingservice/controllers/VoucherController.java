// src/main/java/com/mss301/bookingservice/controllers/VoucherController.java
package com.mss301.bookingservice.controllers;

import com.mss301.bookingservice.enums.DiscountType;
import com.mss301.bookingservice.pojos.Voucher;
import com.mss301.bookingservice.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherRepository voucherRepository;

    @GetMapping
    public ResponseEntity<List<Voucher>> getAllVouchers() {
        return ResponseEntity.ok(voucherRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Voucher>> getActiveVouchers() {
        LocalDateTime now = LocalDateTime.now();
        List<Voucher> activeVouchers = voucherRepository.findByStatusAndStartDateBeforeAndEndDateAfter(
                "ACTIVE", now, now);
        return ResponseEntity.ok(activeVouchers);
    }

    @PostMapping
    public ResponseEntity<Voucher> createVoucher(@RequestBody Voucher voucher) {
        // Kiểm tra mã code đã tồn tại
        if (voucherRepository.existsByCode(voucher.getCode())) {
            throw new RuntimeException("Mã voucher đã tồn tại");
        }
        voucher.setUsedCount(0);
        voucher.setStatus("ACTIVE");
        return ResponseEntity.ok(voucherRepository.save(voucher));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voucher> updateVoucher(@PathVariable Long id, @RequestBody Voucher details) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Voucher với ID: " + id));

        voucher.setCode(details.getCode());
        voucher.setDiscountType(details.getDiscountType());
        voucher.setDiscountValue(details.getDiscountValue());
        voucher.setMinOrderValue(details.getMinOrderValue());
        voucher.setUsageLimit(details.getUsageLimit());
        voucher.setStartDate(details.getStartDate());
        voucher.setEndDate(details.getEndDate());
        voucher.setStatus(details.getStatus());
        voucher.setDescription(details.getDescription());

        return ResponseEntity.ok(voucherRepository.save(voucher));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long id) {
        voucherRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // API kiểm tra voucher hợp lệ (dùng cho frontend)
    @PostMapping("/validate")
    public ResponseEntity<?> validateVoucher(@RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        BigDecimal orderAmount = new BigDecimal(request.get("orderAmount").toString());

        return voucherRepository.findByCode(code)
                .map(voucher -> {
                    Map<String, Object> response = new HashMap<>();

                    // Kiểm tra trạng thái
                    if (!"ACTIVE".equals(voucher.getStatus())) {
                        response.put("valid", false);
                        response.put("message", "Voucher không còn hiệu lực");
                        return ResponseEntity.ok(response);
                    }

                    LocalDateTime now = LocalDateTime.now();

                    // Kiểm tra thời gian
                    if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
                        response.put("valid", false);
                        response.put("message", "Voucher chưa đến ngày áp dụng");
                        return ResponseEntity.ok(response);
                    }
                    if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
                        response.put("valid", false);
                        response.put("message", "Voucher đã hết hạn");
                        return ResponseEntity.ok(response);
                    }

                    // Kiểm tra số lần sử dụng
                    if (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit()) {
                        response.put("valid", false);
                        response.put("message", "Voucher đã hết số lần sử dụng");
                        return ResponseEntity.ok(response);
                    }

                    // Kiểm tra giá trị đơn hàng tối thiểu
                    if (voucher.getMinOrderValue() != null &&
                            orderAmount.compareTo(BigDecimal.valueOf(voucher.getMinOrderValue())) < 0) {
                        response.put("valid", false);
                        response.put("message", String.format("Đơn hàng tối thiểu %s VNĐ để sử dụng voucher",
                                voucher.getMinOrderValue()));
                        return ResponseEntity.ok(response);
                    }

                    // Tính số tiền giảm
                    BigDecimal discountAmount;
                    if (voucher.getDiscountType() == DiscountType.PERCENTAGE) {
                        discountAmount = orderAmount
                                .multiply(BigDecimal.valueOf(voucher.getDiscountValue()))
                                .divide(BigDecimal.valueOf(100));
                    } else {
                        discountAmount = BigDecimal.valueOf(voucher.getDiscountValue());
                    }

                    response.put("valid", true);
                    response.put("message", "Voucher hợp lệ");
                    response.put("discountType", voucher.getDiscountType());
                    response.put("discountValue", voucher.getDiscountValue());
                    response.put("discountAmount", discountAmount);
                    response.put("finalAmount", orderAmount.subtract(discountAmount));

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.ok(Map.of(
                        "valid", false,
                        "message", "Mã voucher không tồn tại"
                )));
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkVoucherValid(@RequestParam String code) {
        return voucherRepository.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(null));
    }
}