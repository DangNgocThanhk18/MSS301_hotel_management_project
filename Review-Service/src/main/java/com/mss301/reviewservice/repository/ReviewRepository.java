package com.mss301.reviewservice.repository;

import com.mss301.reviewservice.enums.ReviewStatus;
import com.mss301.reviewservice.pojos.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Lấy các đánh giá đã được duyệt của một loại phòng
    List<Review> findByRoomTypeIdAndStatusOrderByCreatedAtDesc(Long roomTypeId, ReviewStatus status);
}