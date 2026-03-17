// src/main/java/com/mss301/paymentservice/repositories/PaymentRepository.java
package com.mss301.paymentservice.repositories;

import com.mss301.paymentservice.enums.PaymentStatus;
import com.mss301.paymentservice.pojos.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByReservationId(Long reservationId);
    List<Payment> findByReservationIdAndStatus(Long reservationId, PaymentStatus status);
    Optional<Payment> findByTransactionRef(String transactionRef);
    List<Payment> findByStatus(PaymentStatus status);
}