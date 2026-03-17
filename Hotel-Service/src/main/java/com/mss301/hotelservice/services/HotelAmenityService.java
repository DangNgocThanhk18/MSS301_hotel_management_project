package com.mss301.hotelservice.services;

import com.mss301.hotelservice.dtos.HotelAmenityRequestDTO;
import com.mss301.hotelservice.dtos.HotelAmenityResponseDTO;

import java.util.List;

public interface HotelAmenityService {
    List<HotelAmenityResponseDTO> getAllAmenities();
    List<HotelAmenityResponseDTO> getAmenitiesByHotelId(Long hotelId);
    HotelAmenityResponseDTO getAmenityById(Long id);
    HotelAmenityResponseDTO createAmenity(HotelAmenityRequestDTO requestDTO);
    HotelAmenityResponseDTO updateAmenity(Long id, HotelAmenityRequestDTO requestDTO);
    void deleteAmenity(Long id);
}