package com.mss301.roomservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeResponseDTO {
    private Long id;
    private Long hotelId;
    private String code;
    private String name;
    private String imageUrl;
    private Integer capacity;
    private String bedInfo;
    private BigDecimal basePrice;
    private String description;
}
