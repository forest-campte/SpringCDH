package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.dto.ReviewRequestDTO;
import com.Campmate.DYCampmate.dto.ReviewResponseDTO;
import com.Campmate.DYCampmate.entity.CampingZone;
import com.Campmate.DYCampmate.entity.CustomerEntity;
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
        CustomerEntity customer = customerRepository.findById(request.getCustomersId())
                .orElseThrow(() -> new RuntimeException("고객 정보를 찾을 수 없습니다."));
        CampingZone campingZone = campingZoneRepository.findById(request.getCampingZoneId())
                .orElseThrow(() -> new RuntimeException("캠핑존 정보를 찾을 수 없습니다."));

        ReviewEntity review = ReviewEntity.builder()
                .customer(customer)
                .campingZone(campingZone)
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
}