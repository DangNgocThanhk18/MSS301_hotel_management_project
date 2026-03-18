// src/main/java/com/mss301/bookingservice/client/PaymentClient.java
package com.mss301.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "payment-service", url = "${payment.service.url}/api")
public interface PaymentClient {

    @PostMapping("/payments/process-checkout")
    Map<String, Object> processCheckoutPayment(@RequestBody Map<String, Object> request);

    @GetMapping("/payments/reservation/{reservationId}")
    Map<String, Object> getPaymentsByReservation(@PathVariable("reservationId") Long reservationId);
}