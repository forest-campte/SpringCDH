package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.ReviewEntity;
import lombok.Builder;

// 리뷰 목록 응답 DTO
@Builder
public record ReviewDTO(
        Long reviewId,
        String authorName, // Customer의 닉네임
        String content,
        Double rating
) {
    // ReviewEntity를 이 DTO로 변환
    public static ReviewDTO fromEntity(ReviewEntity review) {
        return ReviewDTO.builder()
                .reviewId(review.getId())
                .authorName(review.getCustomer().getNickname())
                .content(review.getComent())
                .rating((double) review.getRating())
                .build();
    }
}
