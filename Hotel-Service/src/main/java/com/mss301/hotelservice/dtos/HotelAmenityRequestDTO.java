package com.mss301.hotelservice.dtos;

import com.mss301.hotelservice.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HotelAmenityRequestDTO {

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotBlank(message = "Amenity name is required")
    private String name;

    private String description;

    private Status status;
}