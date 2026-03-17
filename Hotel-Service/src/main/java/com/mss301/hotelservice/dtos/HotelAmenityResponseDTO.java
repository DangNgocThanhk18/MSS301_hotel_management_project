package com.mss301.hotelservice.dtos;

import com.mss301.hotelservice.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HotelAmenityResponseDTO {
    private Long id;
    private Long hotelId;
    private String name;
    private String description;
    private Status status;
}