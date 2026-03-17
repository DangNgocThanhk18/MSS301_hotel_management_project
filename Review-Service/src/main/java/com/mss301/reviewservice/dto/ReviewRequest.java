package com.mss301.reviewservice.dto;
import lombok.Data;

@Data
public class ReviewRequest {
    private Long roomTypeId;
    private Long customerId;
    private String customerName;
    private Integer rating;
    private String comment;
}