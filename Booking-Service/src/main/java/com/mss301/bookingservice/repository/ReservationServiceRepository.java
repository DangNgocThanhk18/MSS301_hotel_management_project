// src/main/java/com/mss301/bookingservice/repository/ReservationServiceRepository.java
package com.mss301.bookingservice.repository;

import com.mss301.bookingservice.pojos.ReservationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationServiceRepository extends JpaRepository<ReservationService, Long> {
    List<ReservationService> findByReservationId(Long reservationId);
    List<ReservationService> findByReservationIdAndStatus(Long reservationId, String status);
}