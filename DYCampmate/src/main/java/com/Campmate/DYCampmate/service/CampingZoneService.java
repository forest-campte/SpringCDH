package com.Campmate.DYCampmate.service;



import com.Campmate.DYCampmate.dto.*;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CampingZone;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.repository.CampingZoneRepository;
import com.Campmate.DYCampmate.repository.ReviewRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// 로깅 import (홈 화면 디버깅용으로 유지)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampingZoneService {

    private final CampingZoneRepository campingZoneRepository;
    private final AdminRepo adminRepository;
    private final ReviewRepo reviewRepository;

    private static final Logger logger = LoggerFactory.getLogger(CampingZoneService.class);

    /**
     * ✅ [이 메소드 추가됨 - 컨트롤러 에러 해결용]
     */
    public List<CampingZoneDto> getZonesForAdmin(AdminEntity admin) {
        return campingZoneRepository.findByAdmin(admin)
                .stream()
                .map(CampingZoneDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 캠핑존을 관리자(Admin)별로 그룹화하여 조회합니다.
     */
    public List<AdminZoneGroupDTO> getZonesGroupedByAdmin() {
        try {
            List<ZoneHomeViewDTO> campsiteList = adminRepository.findAllAsCampsiteList();
            AdminZoneGroupDTO singleGroup = AdminZoneGroupDTO.builder()
                    .name("전체 캠핑장")
                    .sites(campsiteList)
                    .build();
            return Collections.singletonList(singleGroup);
        } catch (Exception e) {
            logger.error("!!! getZonesGroupedByAdmin 수행 중 에러 발생 !!!", e);
            throw new RuntimeException("getZonesGroupedByAdmin 서비스 로직 실패", e);
        }
    }

    /**
     * ✅ [수정]
     * 캠핑존 상세 정보 조회 (수정 전 원본 로직)
     * reviewCount, admin 등을 DTO로 전달하지 않습니다.
     */
    public CampsiteDetailDTO getCampsiteDetail(Long campsiteId) {
        CampingZone mainZone = campingZoneRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException("Campsite not found: " + campsiteId));

        AdminEntity admin = mainZone.getAdmin();

        // [수정] admin이 null일 경우를 대비한 방어 코드
        List<CampsiteSiteDTO> siteDTOs;
        if (admin != null) {
            List<CampingZone> allSitesFromAdmin = campingZoneRepository.findAllByAdmin(admin);
            siteDTOs = allSitesFromAdmin.stream()
                    .map(CampsiteSiteDTO::fromEntity)
                    .collect(Collectors.toList());
        } else {
            siteDTOs = Collections.emptyList();
        }

        Double averageRating = reviewRepository.findAverageRatingByCampingZoneId(campsiteId);

        // ✅ [수정] 원본 DTO의 fromEntity 메소드 호출
        return CampsiteDetailDTO.fromEntity(
                mainZone,
                averageRating,
                siteDTOs
        );
    }

    /**
     * 특정 캠핑존의 리뷰 목록 조회
     */
    public List<ReviewDTO> getReviews(Long campsiteId) {
        //  findAllByCampingZoneId가 List<ReviewEntity>를 반환
        return reviewRepository.findAllByCampingZoneId(campsiteId)
                .stream()
                .map(ReviewDTO::fromEntity)
                .collect(Collectors.toList());
    }



    //AdminWeb
    public List<CampingZoneDto> getAllCampingZones() {
        return campingZoneRepository.findAll().stream()
                .map(CampingZoneDto::from)
                .collect(Collectors.toList());
    }


    // 캠핑존 생성
    @Transactional
    public CampingZoneDto createCampingZone(AdminEntity admin, CampingZoneSaveRequestDto requestDto) {
//        admin = adminRepository.findById(requestDto.adminId())
//                .orElseThrow(() -> new EntityNotFoundException("Admin not found with id: " + requestDto.adminId()));

        CampingZone newCampingZone = requestDto.toEntity(admin);
        CampingZone savedCampingZone = campingZoneRepository.save(newCampingZone);
        return CampingZoneDto.from(savedCampingZone);
    }

    // 캠핑존 수정
    @Transactional
    public CampingZoneDto updateCampingZone(AdminEntity admin, Long zoneId, CampingZoneUpdateRequestDto requestDto) {
        CampingZone campingZone = campingZoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("CampingZone not found with id: " + zoneId));

        // 권한 확인 (선택적이지만 권장)
        if (!campingZone.getAdmin().getId().equals(admin.getId())) {
            throw new SecurityException("수정할 권한이 없습니다.");
        }

        campingZone.update(
                requestDto.name(),
                requestDto.description(),
                requestDto.capacity(),
                requestDto.price(),
                requestDto.type(),
                requestDto.defaultSize(),
                requestDto.floor(),
                requestDto.parking() != null && requestDto.parking() == 1, // null 체크 추가
                requestDto.isActive() != null && requestDto.isActive() == 1, // null 체크 추가
                requestDto.imageUrl()
        );

        return CampingZoneDto.from(campingZone);
    }
}


// 로그인 시
// 관리자 기준 캠핑존 조회
//    public List<CampingZoneDto> getZonesForAdmin(AdminEntity admin) {
//        return campingZoneRepository.findByAdmin(admin)
//                .stream()
//                .map(CampingZoneDto::from)
//                .collect(Collectors.toList());
//    }

// 전체 캠핑존 조회 (홈 화면용)
//    public List<ZoneHomeViewDTO> getAllCampingZonesWithRating() {
//        return campingZoneRepository.findAllWithAverageRating_Original();
//    }