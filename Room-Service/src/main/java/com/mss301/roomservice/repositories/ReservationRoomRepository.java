// src/main/java/com/mss301/roomservice/repositories/ReservationRoomRepository.java
package com.mss301.roomservice.repositories;

import com.mss301.roomservice.enums.ReservationRoomStatus;
import com.mss301.roomservice.pojos.ReservationRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoom, Long> {

    List<ReservationRoom> findByRoomId(Long roomId);

    List<ReservationRoom> findByReservationId(Long reservationId);

    @Query("SELECT rr FROM ReservationRoom rr WHERE rr.roomId = :roomId " +
            "AND rr.checkInDate < :checkOut AND rr.checkOutDate > :checkIn " +
            "AND rr.status != :cancelledStatus")
    List<ReservationRoom> findConflictingReservations(
            @Param("roomId") Long roomId,
            @Param("checkIn") Date checkIn,
            @Param("checkOut") Date checkOut,
            @Param("cancelledStatus") ReservationRoomStatus cancelledStatus);

    // Overload method với giá trị mặc định
    default List<ReservationRoom> findConflictingReservations(Long roomId, Date checkIn, Date checkOut) {
        return findConflictingReservations(roomId, checkIn, checkOut, ReservationRoomStatus.CANCELLED);
    }
}