package com.mss301.roomservice.dtos;

import lombok.Data;
import java.util.List;

@Data
public class AmenityRequestDTO {
    private List<Long> amenityIds;
}