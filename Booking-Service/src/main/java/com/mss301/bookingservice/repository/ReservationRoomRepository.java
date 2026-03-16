package com.mss301.bookingservice.repository;

import com.mss301.bookingservice.pojos.ReservationRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoom, Long> {
}