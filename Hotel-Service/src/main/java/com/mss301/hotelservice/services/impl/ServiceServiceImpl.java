// src/main/java/com/mss301/hotelservice/services/impl/ServiceServiceImpl.java
package com.mss301.hotelservice.services.impl;

import com.mss301.hotelservice.dtos.ServiceRequestDTO;
import com.mss301.hotelservice.dtos.ServiceResponseDTO;
import com.mss301.hotelservice.exception.ResourceNotFoundException;
import com.mss301.hotelservice.pojos.HotelService;
import com.mss301.hotelservice.repositories.ServiceRepository;
import com.mss301.hotelservice.services.ServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    @Override
    public ServiceResponseDTO createService(ServiceRequestDTO serviceDTO) {
        log.info("Creating new service with code: {}", serviceDTO.getCode());

        // Kiểm tra code đã tồn tại chưa
        if (serviceRepository.existsByCode(serviceDTO.getCode())) {
            log.error("Service with code {} already exists", serviceDTO.getCode());
            throw new RuntimeException("Service with code " + serviceDTO.getCode() + " already exists");
        }

        // Kiểm tra tên service đã tồn tại trong cùng hotel chưa
        List<HotelService> hotelServices = serviceRepository.findByHotelId(serviceDTO.getHotelId());
        boolean duplicateName = hotelServices.stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(serviceDTO.getName()));

        if (duplicateName) {
            log.error("Service with name {} already exists in hotel {}", serviceDTO.getName(), serviceDTO.getHotelId());
            throw new RuntimeException("Service with name '" + serviceDTO.getName() + "' already exists in this hotel");
        }

        HotelService service = mapToEntity(serviceDTO);
        HotelService savedService = serviceRepository.save(service);

        log.info("Service created successfully with id: {}", savedService.getId());
        return mapToDTO(savedService);
    }

    @Override
    public ServiceResponseDTO updateService(Long id, ServiceRequestDTO serviceDTO) {
        log.info("Updating service with id: {}", id);

        HotelService existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        // Kiểm tra code nếu thay đổi
        if (!existingService.getCode().equals(serviceDTO.getCode())) {
            if (serviceRepository.existsByCode(serviceDTO.getCode())) {
                log.error("Service with code {} already exists", serviceDTO.getCode());
                throw new RuntimeException("Service with code " + serviceDTO.getCode() + " already exists");
            }
        }

        // Kiểm tra tên nếu thay đổi (trong cùng hotel, loại trừ service hiện tại)
        if (!existingService.getName().equalsIgnoreCase(serviceDTO.getName())) {
            List<HotelService> hotelServices = serviceRepository.findByHotelId(serviceDTO.getHotelId());
            boolean duplicateName = hotelServices.stream()
                    .filter(s -> !s.getId().equals(id))
                    .anyMatch(s -> s.getName().equalsIgnoreCase(serviceDTO.getName()));

            if (duplicateName) {
                log.error("Service with name {} already exists in hotel {}", serviceDTO.getName(), serviceDTO.getHotelId());
                throw new RuntimeException("Service with name '" + serviceDTO.getName() + "' already exists in this hotel");
            }
        }

        updateEntity(existingService, serviceDTO);
        HotelService updatedService = serviceRepository.save(existingService);

        log.info("Service updated successfully with id: {}", id);
        return mapToDTO(updatedService);
    }

    @Override
    public ServiceResponseDTO getServiceById(Long id) {
        log.info("Fetching service with id: {}", id);

        HotelService service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        return mapToDTO(service);
    }

    @Override
    public ServiceResponseDTO getServiceByCode(String code) {
        log.info("Fetching service with code: {}", code);

        HotelService service = serviceRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with code: " + code));

        return mapToDTO(service);
    }

    @Override
    public List<ServiceResponseDTO> getAllServices() {
        log.info("Fetching all services");

        List<HotelService> services = serviceRepository.findAll();
        return services.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponseDTO> getServicesByHotelId(Long hotelId) {
        log.info("Fetching services for hotel id: {}", hotelId);

        List<HotelService> services = serviceRepository.findByHotelId(hotelId);
        return services.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteService(Long id) {
        log.info("Deleting service with id: {}", id);

        HotelService service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        serviceRepository.delete(service);

        log.info("Service deleted successfully with id: {}", id);
    }

    // Helper methods for mapping
    private HotelService mapToEntity(ServiceRequestDTO dto) {
        HotelService service = new HotelService();
        service.setHotelId(dto.getHotelId());
        service.setCode(dto.getCode().toUpperCase());
        service.setName(dto.getName());
        service.setPrice(dto.getPrice());
        return service;
    }

    private void updateEntity(HotelService service, ServiceRequestDTO dto) {
        service.setHotelId(dto.getHotelId());
        service.setCode(dto.getCode().toUpperCase());
        service.setName(dto.getName());
        service.setPrice(dto.getPrice());
    }

    private ServiceResponseDTO mapToDTO(HotelService service) {
        return ServiceResponseDTO.builder()
                .id(service.getId())
                .hotelId(service.getHotelId())
                .code(service.getCode())
                .name(service.getName())
                .price(service.getPrice())
                .build();
    }
}