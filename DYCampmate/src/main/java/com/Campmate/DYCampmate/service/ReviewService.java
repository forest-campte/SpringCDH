package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.dto.MyReviewResponseDTO;
import com.Campmate.DYCampmate.dto.ReviewRequestDTO;
import com.Campmate.DYCampmate.dto.ReviewResponseDTO;
import com.Campmate.DYCampmate.entity.CampingZone;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.entity.ReservationEntity;
import com.Campmate.DYCampmate.entity.ReviewEntity;
import com.Campmate.DYCampmate.repository.CampingZoneRepository;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import com.Campmate.DYCampmate.repository.ReservationRepo;
import com.Campmate.DYCampmate.repository.ReviewRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//@Transactional
public class ReviewService {
    private final ReviewRepo reviewRepository;
    private final ReservationRepo reservationRepository;
    private final CustomerRepo customerRepository;
    private final CampingZoneRepository campingZoneRepository;

    // 리뷰 등록
    public ReviewResponseDTO createReview(ReviewRequestDTO request) {
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("고객 정보를 찾을 수 없습니다."));
        CampingZone campingZone = campingZoneRepository.findById(request.getCampingZoneId())
                .orElseThrow(() -> new RuntimeException("캠핑존 정보를 찾을 수 없습니다."));
        ReservationEntity reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("예약 정보를 찾을 수 없습니다."));

        ReviewEntity review = ReviewEntity.builder()
                .customer(customer)
                .campingZone(campingZone)
                .reservation(reservation)
                .rating(request.getRating())
                .coment(request.getComent())
                .build();

        ReviewEntity saved = reviewRepository.save(review);

        return ReviewResponseDTO.builder()
                .id(saved.getId())
                .customerName(customer.getNickname())
                .campingZoneName(campingZone.getName())
                .rating(saved.getRating())
                .coment(saved.getComent())
                .createdDt(saved.getCreatedDt())
                .build();
    }

    // 특정 캠핑존 리뷰 조회
    public List<ReviewResponseDTO> getReviewsByCampingZone(Long campingZoneId) {
        CampingZone campingZone = campingZoneRepository.findById(campingZoneId)
                .orElseThrow(() -> new RuntimeException("캠핑존 정보를 찾을 수 없습니다."));

        return reviewRepository.findByCampingZone(campingZone).stream()
                .map(r -> ReviewResponseDTO.builder()
                        .id(r.getId())
                        .customerName(r.getCustomer().getNickname())
                        .campingZoneName(r.getCampingZone().getName())
                        .rating(r.getRating())
                        .coment(r.getComent())
                        .createdDt(r.getCreatedDt())
                        .build())
                .collect(Collectors.toList());
    }

    // 로그인한 유저의 리뷰 조회
    public List<MyReviewResponseDTO> getMyReviewsByCustomerId(Long customerId) {
        // 1. customerId로 CustomerEntity가 존재하는지 확인 (안전 장치)
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("고객 정보를 찾을 수 없습니다: " + customerId);
        }

        // 2. ReviewRepo에서 customerId로 리뷰 엔티티 목록 조회
        List<ReviewEntity> myReviews = reviewRepository.findByCustomerIdOrderByIdDesc(customerId);

        // 3. List<ReviewEntity>를 List<MyReviewResponseDTO>로 변환
        return myReviews.stream()
                .map(review -> MyReviewResponseDTO.builder()
                        .id(review.getId())
                        .campsiteName(review.getCampingZone().getName())
                        .rating(review.getRating())
                        .coment(review.getComent())
                        .createdDt(review.getCreatedDt())
                        .build())
                .collect(Collectors.toList());
    }
}