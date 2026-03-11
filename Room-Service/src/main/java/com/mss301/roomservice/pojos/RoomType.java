package com.mss301.roomservice.pojos;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "room_type")
@Data
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hotelId;

    private String code;

    private String name;

    private Integer capacity;

    private String bedInfo;

    private BigDecimal basePrice;

    private String imageURl;
}

