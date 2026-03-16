package com.mss301.hotelservice.pojos;

import com.mss301.hotelservice.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "service")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
}