package com.mss301.paymentservice.pojos;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "payment")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;

    private BigDecimal amount;

    private String currency;

    private String paymentMethod;

    private String status;
}

