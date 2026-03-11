package com.mss301.paymentservice.pojos;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice")
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;

    private String invoiceNumber;

    private BigDecimal total;

    private BigDecimal tax;

    private String status;
}

