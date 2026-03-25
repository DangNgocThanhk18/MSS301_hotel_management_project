// src/main/java/com/mss301/bookingservice/repositories/ReservationRepository.java
package com.mss301.bookingservice.repository;

import com.mss301.bookingservice.enums.ReservationStatus;
import com.mss301.bookingservice.pojos.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<Reservation> findByActualCheckInDateBetween(Date start, Date end);
    @Query("SELECT r FROM Reservation r WHERE r.status = :status AND r.expectedCheckInDate >= :date")
    List<Reservation> findByStatusAndExpectedCheckInDateGreaterThanEqual(
            @Param("status") ReservationStatus status,
            @Param("date") Date date);

    List<Reservation> findByStatusAndActualCheckInDateIsNull(ReservationStatus status);

}