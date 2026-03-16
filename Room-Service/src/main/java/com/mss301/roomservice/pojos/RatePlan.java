package com.mss301.roomservice.pojos;

import com.mss301.roomservice.enums.RateType;
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

    @Enumerated(EnumType.STRING)
    private RateType rateType;
}