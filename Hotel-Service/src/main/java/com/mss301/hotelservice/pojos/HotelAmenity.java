package com.mss301.hotelservice.pojos;

import com.mss301.hotelservice.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "hotel_amenity")
@Data
public class HotelAmenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hotelId;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
}