// src/main/java/com/mss301/hotelservice/controllers/ServiceController.java
package com.mss301.hotelservice.controllers;

import com.mss301.hotelservice.dtos.ServiceRequestDTO;
import com.mss301.hotelservice.dtos.ServiceResponseDTO;
import com.mss301.hotelservice.services.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Cho phép React app kết nối
public class ServiceController {

    private final ServiceService serviceService;

    // GET /api/services - Lấy tất cả services
    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> getAllServices() {
        List<ServiceResponseDTO> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    // GET /api/services/{id} - Lấy service theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable Long id) {
        try {
            ServiceResponseDTO service = serviceService.getServiceById(id);
            return ResponseEntity.ok(service);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Service not found with id: " + id));
        }
    }

    // GET /api/services/code/{code} - Lấy service theo code
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getServiceByCode(@PathVariable String code) {
        try {
            ServiceResponseDTO service = serviceService.getServiceByCode(code);
            return ResponseEntity.ok(service);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Service not found with code: " + code));
        }
    }

    // GET /api/services/hotel/{hotelId} - Lấy services theo hotel ID
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ServiceResponseDTO>> getServicesByHotelId(@PathVariable Long hotelId) {
        List<ServiceResponseDTO> services = serviceService.getServicesByHotelId(hotelId);
        return ResponseEntity.ok(services);
    }

    // POST /api/services - Tạo service mới
    @PostMapping
    public ResponseEntity<?> createService(@Valid @RequestBody ServiceRequestDTO serviceDTO) {
        try {
            ServiceResponseDTO createdService = serviceService.createService(serviceDTO);
            return new ResponseEntity<>(createdService, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // PUT /api/services/{id} - Cập nhật service
    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequestDTO serviceDTO) {
        try {
            ServiceResponseDTO updatedService = serviceService.updateService(id, serviceDTO);
            return ResponseEntity.ok(updatedService);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(e.getMessage()));
            }
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // DELETE /api/services/{id} - Xóa service
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        try {
            serviceService.deleteService(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Inner class for error response (định dạng lỗi giống với frontend mong đợi)
    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}