package com.mss301.roomservice.services.impl;

import com.mss301.roomservice.clients.HotelAmenityClient;
import com.mss301.roomservice.dtos.AmenityResponseDTO;
import com.mss301.roomservice.dtos.RoomTypeRequestDTO;
import com.mss301.roomservice.dtos.RoomTypeResponseDTO;
import com.mss301.roomservice.exception.ResourceNotFoundException;
import com.mss301.roomservice.pojos.Amenity;
import com.mss301.roomservice.pojos.RoomType;
import com.mss301.roomservice.repositories.AmenityRepository;
import com.mss301.roomservice.repositories.RoomTypeRepository;
import com.mss301.roomservice.services.RoomTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final AmenityRepository amenityRepository;
    private final HotelAmenityClient hotelAmenityClient;

    @Override
    public RoomTypeResponseDTO createRoomType(RoomTypeRequestDTO request) {
        if (roomTypeRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Room type with code " + request.getCode() + " already exists");
        }

        RoomType roomType = new RoomType();
        roomType.setHotelId(request.getHotelId());
        roomType.setCode(request.getCode());
        roomType.setName(request.getName());
        roomType.setImageUrl(request.getImageUrl());
        roomType.setCapacity(request.getCapacity());
        roomType.setBedInfo(request.getBedInfo());
        roomType.setBasePrice(request.getBasePrice());
        roomType.setDescription(request.getDescription());

        RoomType saved = roomTypeRepository.save(roomType);
        return convertToDTO(saved);
    }

    @Override
    public RoomTypeResponseDTO updateRoomType(Long id, RoomTypeRequestDTO request) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found with id: " + id));

        if (!roomType.getCode().equals(request.getCode()) &&
                roomTypeRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Room type with code " + request.getCode() + " already exists");
        }

        roomType.setHotelId(request.getHotelId());
        roomType.setCode(request.getCode());
        roomType.setName(request.getName());
        roomType.setImageUrl(request.getImageUrl());
        roomType.setCapacity(request.getCapacity());
        roomType.setBedInfo(request.getBedInfo());
        roomType.setBasePrice(request.getBasePrice());

        roomType.setDescription(request.getDescription());

        RoomType updated = roomTypeRepository.save(roomType);
        return convertToDTO(updated);
    }

    @Override
    public RoomTypeResponseDTO getRoomTypeById(Long id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found with id: " + id));
        return convertToDTO(roomType);
    }

    @Override
    public List<RoomTypeResponseDTO> getAllRoomTypes() {
        return roomTypeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomTypeResponseDTO> getRoomTypesByHotelId(Long hotelId) {
        return roomTypeRepository.findByHotelId(hotelId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRoomType(Long id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found with id: " + id));
        roomTypeRepository.delete(roomType);
    }

    private RoomTypeResponseDTO convertToDTO(RoomType roomType) {
        RoomTypeResponseDTO dto = new RoomTypeResponseDTO();
        dto.setId(roomType.getId());
        dto.setHotelId(roomType.getHotelId());
        dto.setCode(roomType.getCode());
        dto.setName(roomType.getName());
        dto.setImageUrl(roomType.getImageUrl());
        dto.setCapacity(roomType.getCapacity());
        dto.setBedInfo(roomType.getBedInfo());
        dto.setBasePrice(roomType.getBasePrice());
        dto.setDescription(roomType.getDescription());
        dto.setAmenities(roomType.getAmenities());
        return dto;
    }

    @Override
    public void updateAmenities(Long roomTypeId, List<Long> amenityIds) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy RoomType với ID: " + roomTypeId));

        List<Amenity> amenityList = amenityRepository.findAllById(amenityIds);
        roomType.setAmenities(new HashSet<>(amenityList));
        roomTypeRepository.save(roomType);
    }

    @Override
    @Transactional
    public void updateRoomTypeAmenities(Long roomTypeId, List<Long> amenityIds) {
        // 1. Tìm RoomType hiện có
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại phòng ID: " + roomTypeId));

        Set<Amenity> updatedAmenities = new HashSet<>();

        for (Long id : amenityIds) {
            // 2. Kiểm tra xem Amenity này đã tồn tại trong DB của Room-Service chưa
            Amenity amenity = amenityRepository.findById(id).orElseGet(() -> {

                AmenityResponseDTO externalData = hotelAmenityClient.getAmenityById(id);

                Amenity newAmenity = new Amenity();
                newAmenity.setId(externalData.getId());
                newAmenity.setName(externalData.getName());
                newAmenity.setHotelId(externalData.getHotelId());
                return amenityRepository.save(newAmenity);
            });
            updatedAmenities.add(amenity);
        }

        // 5. Cập nhật danh sách tiện ích và lưu lại
        roomType.getAmenities().clear();
        roomType.getAmenities().addAll(updatedAmenities);
        roomTypeRepository.save(roomType);
    }
}