package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.ReviewRequestDTO;
import com.Campmate.DYCampmate.dto.ReviewResponseDTO;
import com.Campmate.DYCampmate.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 등록
    @PostMapping("/submit")
    public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewRequestDTO request) {
        return ResponseEntity.ok(reviewService.createReview(request));
    }

    // 캠핑존별 리뷰 조회
    @GetMapping("/camping-zone/{campingZoneId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByCampingZone(@PathVariable Long campingZoneId) {
        return ResponseEntity.ok(reviewService.getReviewsByCampingZone(campingZoneId));
    }
}
