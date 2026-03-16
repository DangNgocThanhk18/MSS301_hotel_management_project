package com.mss301.bookingservice.pojos;

import com.mss301.bookingservice.enums.ReservationRoomStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "reservation_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private Long roomId;

    private Long roomTypeId;

    private BigDecimal nightlyPrice;

    @Enumerated(EnumType.STRING)
    private ReservationRoomStatus status;
}