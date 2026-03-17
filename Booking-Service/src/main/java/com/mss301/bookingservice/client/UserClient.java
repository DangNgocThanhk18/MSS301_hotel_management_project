// src/main/java/com/mss301/bookingservice/client/UserClient.java
package com.mss301.bookingservice.client;

import com.mss301.bookingservice.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", url = "${gateway.url}/api")
public interface UserClient {

    @GetMapping("/auth/user-info")
    UserInfoDTO getUserByToken(@RequestHeader("Authorization") String token);
}