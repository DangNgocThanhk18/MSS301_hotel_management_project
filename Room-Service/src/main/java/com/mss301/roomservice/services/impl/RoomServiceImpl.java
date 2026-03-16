package com.mss301.roomservice.services.impl;

import com.mss301.roomservice.dtos.RoomRequestDTO;
import com.mss301.roomservice.pojos.Room;
import com.mss301.roomservice.pojos.RoomType;
import com.mss301.roomservice.repositories.RoomRepository;
import com.mss301.roomservice.repositories.RoomTypeRepository;
import com.mss301.roomservice.services.RoomService;
import com.mss301.roomservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));
    }

    @Override
    public Room createRoom(RoomRequestDTO request) {
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại phòng với ID: " + request.getRoomTypeId()));

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        room.setStatus(com.mss301.roomservice.enums.RoomStatus.valueOf(request.getStatus().toUpperCase()));
        room.setDescription(request.getDescription());
        room.setRoomType(roomType);


        return roomRepository.save(room);
    }

    @Override
    public Room updateRoom(Long id, RoomRequestDTO request) {
        Room room = getRoomById(id);

        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại phòng với ID: " + request.getRoomTypeId()));

        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        room.setStatus(com.mss301.roomservice.enums.RoomStatus.valueOf(request.getStatus().toUpperCase()));
        room.setDescription(request.getDescription());
        room.setRoomType(roomType);

        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }
}