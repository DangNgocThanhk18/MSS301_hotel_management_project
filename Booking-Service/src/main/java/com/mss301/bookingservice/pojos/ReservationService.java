package com.mss301.bookingservice.pojos;

import com.mss301.bookingservice.enums.ReservationServiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "reservation_service")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private Long serviceId;

    private Integer quantity;

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private ReservationServiceStatus status;
}