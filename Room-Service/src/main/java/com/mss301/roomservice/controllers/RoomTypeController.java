package com.mss301.roomservice.controllers;

import com.mss301.roomservice.dtos.RoomTypeRequestDTO;
import com.mss301.roomservice.dtos.RoomTypeResponseDTO;
import com.mss301.roomservice.services.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/room-type")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @PostMapping
    public ResponseEntity<RoomTypeResponseDTO> createRoomType(@RequestBody RoomTypeRequestDTO request) {
        RoomTypeResponseDTO response = roomTypeService.createRoomType(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomTypeResponseDTO> updateRoomType(
            @PathVariable Long id,
            @RequestBody RoomTypeRequestDTO request) {
        RoomTypeResponseDTO response = roomTypeService.updateRoomType(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeResponseDTO> getRoomTypeById(@PathVariable Long id) {
        RoomTypeResponseDTO response = roomTypeService.getRoomTypeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RoomTypeResponseDTO>> getAllRoomTypes() {
        List<RoomTypeResponseDTO> responses = roomTypeService.getAllRoomTypes();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomTypeResponseDTO>> getRoomTypesByHotelId(@PathVariable Long hotelId) {
        List<RoomTypeResponseDTO> responses = roomTypeService.getRoomTypesByHotelId(hotelId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return ResponseEntity.noContent().build();
    }
}
