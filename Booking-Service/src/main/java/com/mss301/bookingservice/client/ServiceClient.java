// src/main/java/com/mss301/bookingservice/client/ServiceClient.java
package com.mss301.bookingservice.client;

import com.mss301.bookingservice.dto.ServiceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "hotel-service", url = "${hotel.service.url}/api")
public interface ServiceClient {

    @GetMapping("/services/{id}")
    ServiceDTO getServiceById(@PathVariable("id") Long id);

    @GetMapping("/services")
    List<ServiceDTO> getAllServices();

    @GetMapping("/services/hotel/{hotelId}")
    List<ServiceDTO> getServicesByHotelId(@PathVariable("hotelId") Long hotelId);
}