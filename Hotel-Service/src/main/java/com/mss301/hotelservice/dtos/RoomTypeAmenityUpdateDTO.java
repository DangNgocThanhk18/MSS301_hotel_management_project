package com.mss301.hotelservice.dtos;

import lombok.Data;
import java.util.List;

@Data
public class RoomTypeAmenityUpdateDTO {
    private List<Long> amenityIds;
}