// src/main/java/com/mss301/hotelservice/dtos/ServiceResponseDTO.java
package com.mss301.hotelservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDTO {
    private Long id;
    private Long hotelId;
    private String code;
    private String name;
    private BigDecimal price;
}