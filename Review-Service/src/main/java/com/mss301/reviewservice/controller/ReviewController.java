package com.mss301.reviewservice.controller;

import com.mss301.reviewservice.dto.ReviewRequest;
import com.mss301.reviewservice.enums.ReviewStatus;
import com.mss301.reviewservice.pojos.Review;
import com.mss301.reviewservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;

    // API: Gửi đánh giá mới
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest request) {
        if (request.getRating() < 1 || request.getRating() > 5) {
            return ResponseEntity.badRequest().body("Rating phải từ 1 đến 5 sao.");
        }

        Review review = new Review();
        review.setRoomTypeId(request.getRoomTypeId());
        review.setCustomerId(request.getCustomerId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());
        review.setStatus(ReviewStatus.PUBLISHED);

        return ResponseEntity.ok(reviewRepository.save(review));
    }

    // API: Lấy danh sách đánh giá theo RoomType
    @GetMapping("/room-type/{id}")
    public ResponseEntity<List<Review>> getReviewsByRoom(@PathVariable Long id) {
        return ResponseEntity.ok(
                reviewRepository.findByRoomTypeIdAndStatusOrderByCreatedAtDesc(id, ReviewStatus.PUBLISHED)
        );
    }
}