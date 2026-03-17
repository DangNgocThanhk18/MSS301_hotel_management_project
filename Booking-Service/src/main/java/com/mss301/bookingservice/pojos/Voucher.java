package com.mss301.bookingservice.pojos;

import com.mss301.bookingservice.enums.DiscountType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Data
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Double discountValue;

    private Double minOrderValue;

    private Integer usageLimit;
    private Integer usedCount = 0;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String status = "ACTIVE"; // Trạng thái: ACTIVE, EXPIRED, DISABLED
    private String description;
}