// src/main/java/com/mss301/bookingservice/repositories/GuestRepository.java
package com.mss301.bookingservice.repository;

import com.mss301.bookingservice.pojos.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByReservationId(Long reservationId);
    List<Guest> findByEmail(String email);
    List<Guest> findByDocumentNumber(String documentNumber);
}