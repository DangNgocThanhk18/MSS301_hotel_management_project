// src/main/java/com/mss301/bookingservice/dto/BookingServiceRequestDTO.java
package com.mss301.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingServiceRequestDTO {
    private Long reservationId;
    private List<ServiceItem> services;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceItem {
        private Long serviceId;
        private Integer quantity;
        private String serviceName; // Để hiển thị
        private BigDecimal price;    // Giá tại thời điểm đặt
    }
}