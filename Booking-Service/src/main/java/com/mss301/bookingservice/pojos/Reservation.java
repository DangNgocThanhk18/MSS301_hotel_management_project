// src/main/java/com/mss301/bookingservice/pojos/Reservation.java
package com.mss301.bookingservice.pojos;

import com.mss301.bookingservice.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reservationCode;

    private Long customerId; // ID từ UserService (có thể null nếu khách vãng lai)

    private Long hotelId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedCheckInDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedCheckOutDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date actualCheckInDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date actualCheckOutDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(length = 500)
    private String note;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
}