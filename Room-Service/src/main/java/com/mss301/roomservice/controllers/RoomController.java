package com.mss301.roomservice.controllers;

import com.mss301.roomservice.dtos.RoomRequestDTO;
import com.mss301.roomservice.pojos.Room;
import com.mss301.roomservice.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody RoomRequestDTO request) {
        return new ResponseEntity<>(roomService.createRoom(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody RoomRequestDTO request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/check-availability")
    public boolean checkRoomAvailability(@PathVariable("id") Long roomId) {
        try {
            roomService.getRoomById(roomId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateRoomStatus(@PathVariable("id") Long roomId, @RequestParam("status") String status) {
        Room room = roomService.getRoomById(roomId);
        room.setStatus(com.mss301.roomservice.enums.RoomStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok().build();
    }
}