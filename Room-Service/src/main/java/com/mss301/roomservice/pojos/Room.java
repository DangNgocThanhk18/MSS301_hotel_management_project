package com.mss301.roomservice.pojos;

import com.mss301.roomservice.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "room")
@Data
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hotelId;

    private String roomNumber;

    private Long roomTypeId;

    private Integer floor;

    @Enumerated(EnumType.STRING)
    private RoomStatus status = RoomStatus.AVAILABLE;
}