// src/main/java/com/mss301/bookingservice/pojos/ReservationRoom.java
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

    private Long roomId; // ID phòng từ RoomService

    private Long roomTypeId; // ID loại phòng từ RoomService

    private BigDecimal nightlyPrice; // Giá 1 đêm

    @Enumerated(EnumType.STRING)
    private ReservationRoomStatus status;

    private Integer adultCount; // Số người lớn trong phòng

    private Integer childCount; // Số trẻ em trong phòng
}