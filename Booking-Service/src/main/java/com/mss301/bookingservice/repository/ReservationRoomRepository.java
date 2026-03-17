// src/main/java/com/mss301/bookingservice/repositories/ReservationRoomRepository.java
package com.mss301.bookingservice.repository;

import com.mss301.bookingservice.pojos.ReservationRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoom, Long> {
    List<ReservationRoom> findByReservationId(Long reservationId);
    List<ReservationRoom> findByRoomId(Long roomId);
}