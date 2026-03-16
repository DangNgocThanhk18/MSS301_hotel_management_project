package com.mss301.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "room-service", url = "http://localhost:8003")
public interface RoomClient {

    @GetMapping("/api/rooms/{id}/check-availability")
    boolean checkRoomAvailability(@PathVariable("id") Long roomId);
}