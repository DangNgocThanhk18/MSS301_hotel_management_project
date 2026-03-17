// src/main/java/com/mss301/bookingservice/services/RoomCalculationService.java
package com.mss301.bookingservice.service;

import com.mss301.bookingservice.dto.BookingRequestDTO;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomCalculationService {

    /**
     * Tính số phòng cần dựa trên tổng số khách
     * Công thức: ceil((tổng người lớn + tổng trẻ em * 0.5) / sức chứa phòng)
     */
    public int calculateRequiredRooms(List<BookingRequestDTO.RoomOccupancy> rooms, int roomCapacity) {
        double totalWeight = 0;
        for (BookingRequestDTO.RoomOccupancy room : rooms) {
            totalWeight += room.getAdultCount() + (room.getChildCount() * 0.5);
        }
        return (int) Math.ceil(totalWeight / roomCapacity);
    }

    /**
     * Kiểm tra số phòng từ request có khớp với tính toán không
     */
    public boolean validateRooms(List<BookingRequestDTO.RoomOccupancy> requestedRooms, int roomCapacity) {
        int calculatedRooms = calculateRequiredRooms(requestedRooms, roomCapacity);
        return calculatedRooms == requestedRooms.size();
    }

    /**
     * Tính tổng số người lớn
     */
    public int getTotalAdults(List<BookingRequestDTO.RoomOccupancy> rooms) {
        return rooms.stream().mapToInt(BookingRequestDTO.RoomOccupancy::getAdultCount).sum();
    }

    /**
     * Tính tổng số trẻ em
     */
    public int getTotalChildren(List<BookingRequestDTO.RoomOccupancy> rooms) {
        return rooms.stream().mapToInt(BookingRequestDTO.RoomOccupancy::getChildCount).sum();
    }
}