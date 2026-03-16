
package com.mss301.hotelservice.services;

import com.mss301.hotelservice.dtos.ServiceRequestDTO;
import com.mss301.hotelservice.dtos.ServiceResponseDTO;
import java.util.List;

public interface ServiceService {
    ServiceResponseDTO createService(ServiceRequestDTO serviceDTO);
    ServiceResponseDTO updateService(Long id, ServiceRequestDTO serviceDTO);
    ServiceResponseDTO getServiceById(Long id);
    ServiceResponseDTO getServiceByCode(String code);
    List<ServiceResponseDTO> getAllServices();
    List<ServiceResponseDTO> getServicesByHotelId(Long hotelId);
    void deleteService(Long id);
}