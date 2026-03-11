package com.mss301.roomservice.pojos;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "room_rate")
@Data
public class RoomRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomTypeId;

    private Long ratePlanId;

    private LocalDate date;

    private BigDecimal price;
}

