// src/main/java/com/mss301/bookingservice/client/TaskClient.java
package com.mss301.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "task-service", url = "${gateway.url}/api")
public interface TaskClient {

    @PostMapping("/tasks/cleaning")
    void createCleaningTask(@RequestParam("reservationId") Long reservationId);
}