package com.mss301.roomservice.controllers;

import com.mss301.roomservice.dtos.AmenityRequestDTO;
import com.mss301.roomservice.dtos.RoomTypeAmenityUpdateDTO;
import com.mss301.roomservice.dtos.RoomTypeRequestDTO;
import com.mss301.roomservice.dtos.RoomTypeResponseDTO;
import com.mss301.roomservice.services.RoomTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/room-type")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    // 1. Tạo mới loại phòng
    @PostMapping
    public ResponseEntity<RoomTypeResponseDTO> createRoomType(@RequestBody RoomTypeRequestDTO request) {
        RoomTypeResponseDTO response = roomTypeService.createRoomType(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Cập nhật thông tin loại phòng
    @PutMapping("/{id}")
    public ResponseEntity<RoomTypeResponseDTO> updateRoomType(
            @PathVariable Long id,
            @RequestBody RoomTypeRequestDTO request) {
        RoomTypeResponseDTO response = roomTypeService.updateRoomType(id, request);
        return ResponseEntity.ok(response);
    }

    // 3. Lấy chi tiết một loại phòng
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<RoomTypeResponseDTO> getRoomTypeById(@PathVariable Long id) {
        RoomTypeResponseDTO response = roomTypeService.getRoomTypeById(id);
        return ResponseEntity.ok(response);
    }

    // 4. Lấy tất cả loại phòng
    @GetMapping
    @Transactional
    public ResponseEntity<List<RoomTypeResponseDTO>> getAllRoomTypes() {
        List<RoomTypeResponseDTO> responses = roomTypeService.getAllRoomTypes();
        return ResponseEntity.ok(responses);
    }

    // 5. Lấy danh sách loại phòng theo Hotel ID
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomTypeResponseDTO>> getRoomTypesByHotelId(@PathVariable Long hotelId) {
        List<RoomTypeResponseDTO> responses = roomTypeService.getRoomTypesByHotelId(hotelId);
        return ResponseEntity.ok(responses);
    }

    // 6. Xóa loại phòng
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/amenities")
    public ResponseEntity<?> updateRoomTypeAmenities(
            @PathVariable Long id,
            @RequestBody RoomTypeAmenityUpdateDTO dto) {
        roomTypeService.updateRoomTypeAmenities(id, dto.getAmenityIds());
        return ResponseEntity.ok("Cập nhật tiện nghi thành công");
    }
}