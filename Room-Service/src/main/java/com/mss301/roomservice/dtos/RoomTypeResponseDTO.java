package com.mss301.roomservice.dtos;

import com.mss301.roomservice.pojos.Amenity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Set;

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
    private Set<Amenity> amenities;
}