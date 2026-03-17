// src/main/java/com/mss301/bookingservice/dto/RoomTypeDTO.java
package com.mss301.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeDTO {
    private Long id;
    private Long hotelId;
    private String code;
    private String name;
    private Integer capacity;
    private String bedInfo;
    private BigDecimal basePrice;
    private String description;
}