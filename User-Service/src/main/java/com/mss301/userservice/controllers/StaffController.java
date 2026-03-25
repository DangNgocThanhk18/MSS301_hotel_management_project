package com.mss301.userservice.controllers;

import com.mss301.userservice.dto.StaffRequestDTO;
import com.mss301.userservice.dto.StaffResponseDTO;
import com.mss301.userservice.enums.AccountStatus;
import com.mss301.userservice.enums.UserRole;
import com.mss301.userservice.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/staff/housekeeping")
    public ResponseEntity<List<StaffResponseDTO>> getHousekeepingStaff() {
        List<StaffResponseDTO> allStaff = staffService.getAllStaff();
        List<StaffResponseDTO> housekeepingStaff = allStaff.stream()
                .filter(staff -> UserRole.HOUSEKEEPING.equals(staff.getRole()) &&
                        AccountStatus.ACTIVE.equals(staff.getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(housekeepingStaff);
    }

        @GetMapping("/staff")
    public ResponseEntity<List<StaffResponseDTO>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @PostMapping
    public ResponseEntity<?> createStaff(@RequestBody StaffRequestDTO requestDTO) {
        try {
            StaffResponseDTO createdStaff = staffService.createStaff(requestDTO);
            return new ResponseEntity<>(createdStaff, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/details")
    public ResponseEntity<?> updateStaffDetails(@PathVariable Long id, @RequestBody StaffRequestDTO requestDTO) {
        try {
            StaffResponseDTO updatedStaff = staffService.updateStaff(id, requestDTO);
            return ResponseEntity.ok(updatedStaff);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStaffStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("newStatus");
            staffService.updateStaffStatus(id, status);
            return ResponseEntity.ok("Cập nhật trạng thái thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        try {

            StaffRequestDTO resetDTO = new StaffRequestDTO();
            resetDTO.setPassword("123456");
            staffService.updateStaff(id, resetDTO);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Đặt lại mật khẩu thành công! Mật khẩu mặc định: 123456.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}