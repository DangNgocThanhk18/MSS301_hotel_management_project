package com.mss301.roomservice.services.impl;

import com.mss301.roomservice.dtos.RoomRequestDTO;
import com.mss301.roomservice.enums.ReservationRoomStatus;
import com.mss301.roomservice.enums.RoomStatus;
import com.mss301.roomservice.exception.ResourceNotFoundException;
import com.mss301.roomservice.pojos.ReservationRoom;
import com.mss301.roomservice.pojos.Room;
import com.mss301.roomservice.pojos.RoomType;
import com.mss301.roomservice.repositories.ReservationRoomRepository;
import com.mss301.roomservice.repositories.RoomRepository;
import com.mss301.roomservice.repositories.RoomTypeRepository;
import com.mss301.roomservice.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ReservationRoomRepository reservationRoomRepository;

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    @Override
    public Room createRoom(RoomRequestDTO request) {
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("RoomType not found"));

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());

        // SỬA: Chuyển String thành Enum
        if (request.getStatus() != null) {
            try {
                room.setStatus(RoomStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid room status: " + request.getStatus() +
                        ". Allowed values: AVAILABLE, OCCUPIED, MAINTENANCE, CLEANING");
            }
        } else {
            room.setStatus(RoomStatus.AVAILABLE); // Mặc định
        }

        room.setRoomType(roomType);

        return roomRepository.save(room);
    }

    @Override
    public Room updateRoom(Long id, RoomRequestDTO request) {
        Room room = getRoomById(id);

        if (request != null) {
            if (request.getRoomNumber() != null) {
                room.setRoomNumber(request.getRoomNumber());
            }

            if (request.getFloor() != null) {
                room.setFloor(request.getFloor());
            }

            // SỬA LỖI Ở ĐÂY - Chuyển String thành Enum
            if (request.getStatus() != null) {
                try {
                    // Chuyển String thành Enum bằng valueOf()
                    RoomStatus status = RoomStatus.valueOf(request.getStatus().toUpperCase());
                    room.setStatus(status);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid room status: " + request.getStatus() +
                            ". Allowed values: AVAILABLE, OCCUPIED, MAINTENANCE, CLEANING");
                }
            }

            if (request.getRoomTypeId() != null) {
                RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("RoomType not found with id: " + request.getRoomTypeId()));
                room.setRoomType(roomType);
            }
        }

        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }

    @Override
    public List<Long> findAvailableRooms(Long roomTypeId, Date checkIn, Date checkOut, int count) {
        log.info("Finding available rooms for roomTypeId: {}, from {} to {}, need {} rooms",
                roomTypeId, checkIn, checkOut, count);

        return roomRepository.findAvailableRooms(
                roomTypeId, checkIn, checkOut, PageRequest.of(0, count));
    }

    // Trong RoomServiceImpl.java - Cập nhật method bookRoom

    @Override
    public void bookRoom(Long reservationId, Long roomId, Date checkIn, Date checkOut) {
        log.info("Booking room {} for reservation {}", roomId, reservationId);

        // Kiểm tra phòng có tồn tại không
        Room room = getRoomById(roomId);

        // Kiểm tra xem phòng có bị trùng không
        List<ReservationRoom> conflicts = reservationRoomRepository.findConflictingReservations(
                roomId, checkIn, checkOut);

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Room " + roomId + " is not available for the selected dates");
        }

        // Tạo reservation room record
        ReservationRoom reservationRoom = new ReservationRoom();
        reservationRoom.setReservationId(reservationId);
        reservationRoom.setRoomId(roomId);
        reservationRoom.setCheckInDate(checkIn);
        reservationRoom.setCheckOutDate(checkOut);
        reservationRoom.setStatus(ReservationRoomStatus.BOOKED); // SỬA: Dùng Enum

        reservationRoomRepository.save(reservationRoom);

        log.info("Room {} booked successfully for reservation {}", roomId, reservationId);
    }

    // Thêm method cập nhật trạng thái
    @Override
    public void updateRoomBookingStatus(Long reservationId, ReservationRoomStatus newStatus) {
        List<ReservationRoom> bookings = reservationRoomRepository.findByReservationId(reservationId);
        for (ReservationRoom booking : bookings) {
            booking.setStatus(newStatus);
            reservationRoomRepository.save(booking);
        }
        log.info("Updated status for reservation {} to {}", reservationId, newStatus);
    }
}