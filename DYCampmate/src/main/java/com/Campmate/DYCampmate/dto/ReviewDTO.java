package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.ReviewEntity;
import lombok.Builder;

import java.time.LocalDateTime;


// 리뷰 목록 응답 DTO
@Builder
public record ReviewDTO(
//        Long reviewId,
//        String authorName, // Customer의 닉네임
//        String content,
//        Double rating

        Long id,
        String authorName,
        Integer rating,
        String content,
        String imageUrl,
        LocalDateTime createdDt

) {
    // ReviewEntity를 이 DTO로 변환
    public static ReviewDTO fromEntity(ReviewEntity review) {
        String author = review.getCustomer() != null ? review.getCustomer().getNickname() : "익명";
        return ReviewDTO.builder()
//                .reviewId(review.getId())
//                .authorName(review.getCustomer().getNickname())
//                .content(review.getComent())
//                .rating((double) review.getRating())
//                .build();
                .id(review.getId())
                .authorName(author)
                .rating(review.getRating())
                .content(review.getComent())
                .imageUrl(review.getImageUrl())
                .createdDt(review.getCreatedDt())
                .build();
    }
}
