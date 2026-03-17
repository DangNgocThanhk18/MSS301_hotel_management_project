package com.mss301.bookingservice.controllers;

import com.mss301.bookingservice.pojos.Voucher;
import com.mss301.bookingservice.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherRepository voucherRepository;

    @GetMapping
    public ResponseEntity<List<Voucher>> getAllVouchers() {
        return ResponseEntity.ok(voucherRepository.findAll());
    }

    // Tạo Voucher mới
    @PostMapping
    public ResponseEntity<Voucher> createVoucher(@RequestBody Voucher voucher) {
        // Có thể thêm logic kiểm tra mã code đã tồn tại chưa ở đây
        return ResponseEntity.ok(voucherRepository.save(voucher));
    }

    // Cập nhật Voucher
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

    // Xóa Voucher
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long id) {
        voucherRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkVoucherValid(@RequestParam String code) {
        return voucherRepository.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(null));
    }
}