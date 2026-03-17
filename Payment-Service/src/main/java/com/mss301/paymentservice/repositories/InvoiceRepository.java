// src/main/java/com/mss301/paymentservice/repositories/InvoiceRepository.java
package com.mss301.paymentservice.repositories;

import com.mss301.paymentservice.enums.InvoiceStatus;
import com.mss301.paymentservice.pojos.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByReservationId(Long reservationId);
    List<Invoice> findByStatus(InvoiceStatus status);
}