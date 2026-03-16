package com.mss301.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "task-service", url = "http://localhost:8006")
public interface TaskClient {

    @PostMapping("/api/tasks/create-cleaning-task")
    void createCleaningTask(@RequestParam("reservationId") Long reservationId);
}