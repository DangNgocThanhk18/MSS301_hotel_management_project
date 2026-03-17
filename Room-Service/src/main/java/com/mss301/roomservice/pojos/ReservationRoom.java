// src/main/java/com/mss301/roomservice/pojos/ReservationRoom.java
package com.mss301.roomservice.pojos;

import com.mss301.roomservice.enums.ReservationRoomStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "reservation_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId; // ID từ Booking Service

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "check_in_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkInDate;

    @Column(name = "check_out_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkOutDate;

    @Enumerated(EnumType.STRING) // Lưu dưới dạng String trong DB
    @Column(name = "status", length = 50)
    private ReservationRoomStatus status; // SỬA: Đổi từ String sang Enum

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        if (status == null) {
            status = ReservationRoomStatus.BOOKED; // Mặc định là BOOKED
        }
    }
}