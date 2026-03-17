package com.mss301.reviewservice.pojos;

import com.mss301.reviewservice.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookingId;
    private Long customerId;
    private Long roomTypeId;
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.PUBLISHED;

    private LocalDateTime createdAt = LocalDateTime.now();
}