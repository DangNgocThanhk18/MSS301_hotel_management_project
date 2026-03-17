package com.mss301.roomservice.services;

import com.mss301.roomservice.dtos.RoomTypeRequestDTO;
import com.mss301.roomservice.dtos.RoomTypeResponseDTO;
import java.util.List;

public interface RoomTypeService {
    RoomTypeResponseDTO createRoomType(RoomTypeRequestDTO roomTypeDTO);
    RoomTypeResponseDTO updateRoomType(Long id, RoomTypeRequestDTO roomTypeDTO);
    RoomTypeResponseDTO getRoomTypeById(Long id);
    List<RoomTypeResponseDTO> getAllRoomTypes();
    List<RoomTypeResponseDTO> getRoomTypesByHotelId(Long hotelId);
    void deleteRoomType(Long id);
    void updateAmenities(Long roomTypeId, List<Long> amenityIds);
    void updateRoomTypeAmenities(Long roomTypeId, List<Long> amenityIds);
}
