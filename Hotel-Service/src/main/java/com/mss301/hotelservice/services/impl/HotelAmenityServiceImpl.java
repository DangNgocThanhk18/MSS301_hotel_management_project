package com.mss301.hotelservice.services.impl;

import com.mss301.hotelservice.dtos.HotelAmenityRequestDTO;
import com.mss301.hotelservice.dtos.HotelAmenityResponseDTO;
import com.mss301.hotelservice.enums.Status;
import com.mss301.hotelservice.exception.ResourceNotFoundException;
import com.mss301.hotelservice.pojos.HotelAmenity;
import com.mss301.hotelservice.repositories.HotelAmenityRepository;
import com.mss301.hotelservice.services.HotelAmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelAmenityServiceImpl implements HotelAmenityService {

    private final HotelAmenityRepository repository;

    @Override
    public List<HotelAmenityResponseDTO> getAllAmenities() {
        return repository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HotelAmenityResponseDTO> getAmenitiesByHotelId(Long hotelId) {
        return repository.findByHotelId(hotelId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HotelAmenityResponseDTO getAmenityById(Long id) {
        HotelAmenity amenity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with id: " + id));
        return mapToResponseDTO(amenity);
    }

    @Override
    public HotelAmenityResponseDTO createAmenity(HotelAmenityRequestDTO requestDTO) {
        HotelAmenity amenity = new HotelAmenity();
        amenity.setHotelId(requestDTO.getHotelId());
        amenity.setName(requestDTO.getName());
        amenity.setDescription(requestDTO.getDescription());
        amenity.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : Status.ACTIVE);

        HotelAmenity savedAmenity = repository.save(amenity);
        return mapToResponseDTO(savedAmenity);
    }

    @Override
    public HotelAmenityResponseDTO updateAmenity(Long id, HotelAmenityRequestDTO requestDTO) {
        HotelAmenity amenity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with id: " + id));

        amenity.setHotelId(requestDTO.getHotelId());
        amenity.setName(requestDTO.getName());
        amenity.setDescription(requestDTO.getDescription());
        if (requestDTO.getStatus() != null) {
            amenity.setStatus(requestDTO.getStatus());
        }

        HotelAmenity updatedAmenity = repository.save(amenity);
        return mapToResponseDTO(updatedAmenity);
    }

    @Override
    public void deleteAmenity(Long id) {
        HotelAmenity amenity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with id: " + id));
        repository.delete(amenity);
    }

    private HotelAmenityResponseDTO mapToResponseDTO(HotelAmenity amenity) {
        return HotelAmenityResponseDTO.builder()
                .id(amenity.getId())
                .hotelId(amenity.getHotelId())
                .name(amenity.getName())
                .description(amenity.getDescription())
                .status(amenity.getStatus())
                .build();
    }
}