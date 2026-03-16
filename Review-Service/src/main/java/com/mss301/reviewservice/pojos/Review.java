package com.mss301.reviewservice.pojos;

import com.mss301.reviewservice.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hotelId;

    private Long guestId;

    private Integer rating;

    private String comment;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.PENDING;

    private LocalDateTime createdAt;
}