package com.mss301.roomservice.dtos;

import lombok.Data;

@Data
public class RoomRequestDTO {
    private String roomNumber;
    private Integer floor;
    private String status;
    private String description;
    private Long roomTypeId;
    private Long hotelId;
    private String imageUrl;
}