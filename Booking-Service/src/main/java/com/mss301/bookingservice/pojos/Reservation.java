package com.mss301.bookingservice.pojos;

import com.mss301.bookingservice.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reservation")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reservationCode;

    private Long guestId;

    private Long hotelId;

    private LocalDate arrivalDate;

    private LocalDate departureDate;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
}