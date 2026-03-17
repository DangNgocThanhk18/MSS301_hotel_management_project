// src/main/java/com/mss301/paymentservice/PaymentServiceApplication.java
package com.mss301.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
        System.out.println("🚀 Payment Service is running on port 8008");
        System.out.println("📝 API endpoint: http://localhost:8008/api/payments");
    }
}