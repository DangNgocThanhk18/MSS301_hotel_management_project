// src/main/java/com/mss301/bookingservice/repositories/ReservationRepository.java
package com.mss301.bookingservice.repository;

import com.mss301.bookingservice.enums.ReservationStatus;
import com.mss301.bookingservice.pojos.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerId(Long customerId);
    List<Reservation> findByHotelId(Long hotelId);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByReservationCodeStartingWithAndCreatedDateBetween(
            String prefix, Date startDate, Date endDate);
    List<Reservation> findByStatusAndExpectedCheckInDateGreaterThanEqual(
            ReservationStatus status, Date date);
}