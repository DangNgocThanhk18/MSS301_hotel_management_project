package com.mss301.userservice.controllers;

import com.mss301.userservice.dto.*;
import com.mss301.userservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CustomerRequestDTO requestDTO) {
        try {
            CustomerResponseDTO createdCustomer = customerService.createCustomer(requestDTO);
            return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody CustomerUpdateDTO requestDTO) {
        try {
            CustomerResponseDTO updatedCustomer = customerService.updateCustomer(id, requestDTO);
            return ResponseEntity.ok(updatedCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateCustomerStatus(@PathVariable Long id, @RequestBody CustomerStatusDTO requestDTO) {
        try {
            customerService.updateCustomerStatus(id, requestDTO);
            return ResponseEntity.ok("Cập nhật trạng thái thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok("Xóa khách hàng thành công (Đã chuyển sang INACTIVE).");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}