package com.mss301.roomservice.repositories;

import com.mss301.roomservice.enums.RoomStatus;
import com.mss301.roomservice.pojos.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByRoomTypeId(Long roomTypeId);

    List<Room> findByStatus(RoomStatus status);

    // QUAN TRỌNG: Query tìm phòng trống dựa trên database của Room Service
    @Query("SELECT r.id FROM Room r WHERE r.roomType.id = :roomTypeId AND r.status = 'AVAILABLE' AND r.id NOT IN " +
            "(SELECT rr.roomId FROM ReservationRoom rr WHERE rr.checkInDate < :checkOut " +
            "AND rr.checkOutDate > :checkIn AND rr.status != 'CANCELLED')")
    List<Long> findAvailableRooms(
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkIn") Date checkIn,
            @Param("checkOut") Date checkOut,
            Pageable pageable);

    // Đếm số phòng trống
    @Query("SELECT COUNT(r) FROM Room r WHERE r.roomType.id = :roomTypeId AND r.status = 'AVAILABLE' AND r.id NOT IN " +
            "(SELECT rr.roomId FROM ReservationRoom rr WHERE rr.checkInDate < :checkOut " +
            "AND rr.checkOutDate > :checkIn AND rr.status != 'CANCELLED')")
    long countAvailableRooms(
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkIn") Date checkIn,
            @Param("checkOut") Date checkOut);
}