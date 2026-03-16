package com.mss301.hotelservice.pojos;

import com.mss301.hotelservice.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "hotel")
@Data
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    private String description;

    private String address;

    private String city;

    private String country;

    private String phone;

    private String email;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
}