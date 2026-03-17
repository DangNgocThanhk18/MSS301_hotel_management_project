package com.mss301.roomservice.clients;

import com.mss301.roomservice.dtos.AmenityResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hotel-service", url = "http://localhost:8002/api/hotel-amenities")
public interface HotelAmenityClient {
    @GetMapping("/{id}")
    AmenityResponseDTO getAmenityById(@PathVariable("id") Long id);
}