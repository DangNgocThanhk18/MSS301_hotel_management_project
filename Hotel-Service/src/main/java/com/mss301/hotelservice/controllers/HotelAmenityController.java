package com.mss301.hotelservice.controllers;

import com.mss301.hotelservice.dtos.HotelAmenityRequestDTO;
import com.mss301.hotelservice.dtos.HotelAmenityResponseDTO;
import com.mss301.hotelservice.services.HotelAmenityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotel-amenities")
@RequiredArgsConstructor
public class HotelAmenityController {

    private final HotelAmenityService hotelAmenityService;

    // GET /api/hotel-amenities?hotelId=1
    @GetMapping
    public ResponseEntity<List<HotelAmenityResponseDTO>> getAmenities(
            @RequestParam(required = false) Long hotelId) {
        if (hotelId != null) {
            return ResponseEntity.ok(hotelAmenityService.getAmenitiesByHotelId(hotelId));
        }
        return ResponseEntity.ok(hotelAmenityService.getAllAmenities());
    }

    // GET /api/hotel-amenities/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getAmenityById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(hotelAmenityService.getAmenityById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // POST /api/hotel-amenities
    @PostMapping
    public ResponseEntity<?> createAmenity(@Valid @RequestBody HotelAmenityRequestDTO requestDTO) {
        try {
            HotelAmenityResponseDTO createdAmenity = hotelAmenityService.createAmenity(requestDTO);
            return new ResponseEntity<>(createdAmenity, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // PUT /api/hotel-amenities/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAmenity(
            @PathVariable Long id,
            @Valid @RequestBody HotelAmenityRequestDTO requestDTO) {
        try {
            return ResponseEntity.ok(hotelAmenityService.updateAmenity(id, requestDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // DELETE /api/hotel-amenities/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAmenity(@PathVariable Long id) {
        try {
            hotelAmenityService.deleteAmenity(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}