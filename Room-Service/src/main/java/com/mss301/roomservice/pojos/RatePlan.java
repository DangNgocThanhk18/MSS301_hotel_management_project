package com.mss301.roomservice.pojos;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rate_plan")
@Data
public class RatePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hotelId;

    private String code;

    private String name;

    private String rateType;
}

