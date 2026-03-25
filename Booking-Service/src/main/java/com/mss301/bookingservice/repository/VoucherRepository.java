// src/main/java/com/mss301/bookingservice/repository/VoucherRepository.java
package com.mss301.bookingservice.repository;

import com.mss301.bookingservice.pojos.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);
    boolean existsByCode(String code);

    List<Voucher> findByStatus(String status);

    List<Voucher> findByStatusAndStartDateBeforeAndEndDateAfter(
            String status, LocalDateTime startDate, LocalDateTime endDate);
}