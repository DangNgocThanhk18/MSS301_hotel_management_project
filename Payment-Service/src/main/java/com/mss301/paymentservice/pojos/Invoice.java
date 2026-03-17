// src/main/java/com/mss301/paymentservice/pojos/Invoice.java
package com.mss301.paymentservice.pojos;

import com.mss301.paymentservice.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "invoice_number", unique = true, length = 50)
    private String invoiceNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Column(precision = 19, scale = 2)
    private BigDecimal tax;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private InvoiceStatus status;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (invoiceNumber == null) {
            invoiceNumber = "INV-" + System.currentTimeMillis();
        }
    }
}