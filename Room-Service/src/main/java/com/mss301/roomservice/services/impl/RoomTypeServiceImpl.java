package com.mss301.roomservice.services.impl;

import com.mss301.roomservice.dtos.RoomTypeRequestDTO;
import com.mss301.roomservice.dtos.RoomTypeResponseDTO;
import com.mss301.roomservice.exception.ResourceNotFoundException;
import com.mss301.roomservice.pojos.RoomType;
import com.mss301.roomservice.repositories.RoomTypeRepository;
import com.mss301.roomservice.services.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

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

        RoomType saved = roomTypeRepository.save(roomType);
        return convertToDTO(saved);
    }

    @Override
    public RoomTypeResponseDTO updateRoomType(Long id, RoomTypeRequestDTO request) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found with id: " + id));

        // Kiểm tra code nếu thay đổi
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
        return new RoomTypeResponseDTO(
                roomType.getId(),
                roomType.getHotelId(),
                roomType.getCode(),
                roomType.getName(),
                roomType.getImageUrl(),
                roomType.getCapacity(),
                roomType.getBedInfo(),
                roomType.getBasePrice()
        );
    }
}
