package com.mss301.bookingservice.pojos;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "reservation_room")
@Data
public class ReservationRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;

    private Long roomId;

    private Long roomTypeId;

    private BigDecimal nightlyPrice;
}
