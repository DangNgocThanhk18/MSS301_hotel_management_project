package com.mss301.bookingservice.pojos;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "reservation_service")
@Data
public class ReservationService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;

    private Long serviceId;

    private Integer quantity;

    private BigDecimal totalPrice;
}

