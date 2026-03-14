// src/main/java/com/mss301/roomservice/RoomServiceApplication.java
package com.mss301.roomservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RoomServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoomServiceApplication.class, args);
        System.out.println("🚀 Room Service is running on port 8083");
        System.out.println("📝 API endpoint: http://localhost:8083/api/room-type");
    }
}