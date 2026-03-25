// src/main/java/com/mss301/bookingservice/pojos/Reservation.java
package com.mss301.bookingservice.pojos;

import com.mss301.bookingservice.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "reservation")
@Data
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reservationCode;
    private Long customerId;
    private Long hotelId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedCheckInDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedCheckOutDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date actualCheckInDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date actualCheckOutDate;

    private String note;

    private BigDecimal totalAmount;           // Số tiền sau tất cả giảm giá
    private BigDecimal originalAmount;        // Số tiền gốc
    private BigDecimal weekendDiscountAmount; // Số tiền giảm giá cuối tuần
    private Boolean weekendDiscountApplied;   // Đã áp dụng giảm giá cuối tuần chưa
    private BigDecimal discountAmount;        // Số tiền giảm từ voucher
    private String voucherCode;               // Mã voucher đã áp dụng

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
}