// src/main/java/com/mss301/hotelservice/HotelServiceApplication.java
package com.mss301.hotelservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HotelServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelServiceApplication.class, args);
        System.out.println("🚀 Hotel Service is running on port 8002");
        System.out.println("📝 Service API endpoint: http://localhost:8002/api/services");
    }
}