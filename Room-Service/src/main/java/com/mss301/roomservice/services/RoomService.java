// src/main/java/com/mss301/roomservice/services/RoomService.java
package com.mss301.roomservice.services;

import com.mss301.roomservice.dtos.RoomRequestDTO;
import com.mss301.roomservice.enums.ReservationRoomStatus;
import com.mss301.roomservice.pojos.Room;

import java.util.Date;
import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();
    Room getRoomById(Long id);
    Room createRoom(RoomRequestDTO request);
    Room updateRoom(Long id, RoomRequestDTO request);
    void deleteRoom(Long id);

    // Các method liên quan đến đặt phòng
    List<Long> findAvailableRooms(Long roomTypeId, Date checkIn, Date checkOut, int count);
    void bookRoom(Long reservationId, Long roomId, Date checkIn, Date checkOut);
    void updateRoomBookingStatus(Long reservationId, ReservationRoomStatus newStatus); // Thêm mới
}