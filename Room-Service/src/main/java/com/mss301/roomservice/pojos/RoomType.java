// src/main/java/com/mss301/roomservice/pojos/RoomType.java
package com.mss301.roomservice.pojos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "room_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotel_id")
    private Long hotelId;

    @Column(unique = true)
    private String code;

    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    private Integer capacity;

    @Column(name = "bed_info")
    private String bedInfo;

    @Column(name = "base_price")
    private BigDecimal basePrice;
}