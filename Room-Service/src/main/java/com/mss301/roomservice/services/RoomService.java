package com.mss301.roomservice.services;

import com.mss301.roomservice.dtos.RoomRequestDTO;
import com.mss301.roomservice.pojos.Room;
import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();
    Room getRoomById(Long id);
    Room createRoom(RoomRequestDTO roomRequestDTO);
    Room updateRoom(Long id, RoomRequestDTO roomRequestDTO);
    void deleteRoom(Long id);
}