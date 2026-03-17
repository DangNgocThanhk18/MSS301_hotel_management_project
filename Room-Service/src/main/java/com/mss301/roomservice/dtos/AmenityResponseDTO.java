package com.mss301.roomservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmenityResponseDTO {
    private Long id;
    private Long hotelId;
    private String name;
    private String description;
}