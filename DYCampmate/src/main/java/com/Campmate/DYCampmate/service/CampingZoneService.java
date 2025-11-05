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
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampingZoneService {

    private final CampingZoneRepository campingZoneRepository;
    private final AdminRepo adminRepository;
    private final ReviewRepo reviewRepository;

    private static final Logger logger = LoggerFactory.getLogger(CampingZoneService.class);
    private final FileStorageService fileStorageService;

    /**
     * ✅ [이 메소드 추가됨 - 컨트롤러 에러 해결용]
     */
//    public List<CampingZoneDto> getZonesForAdmin(AdminEntity admin) {
//        return campingZoneRepository.findByAdmin(admin)
//                .stream()
//                .map(CampingZoneDto::from)
//                .collect(Collectors.toList());
//    }
    @Transactional(readOnly = true) // GET 메서드에 개별적으로 추가
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

    // === [신규] React 앱용 (FormData + 파일) ===
    @Transactional
    public CampingZoneDto createCampingZoneWithForm(AdminEntity admin, CampingZoneFormDto dto, MultipartFile imageFile) {

        // 1. S3에 파일 저장 (파일 없으면 null 반환됨)
        String imageUrl = fileStorageService.storeFile(imageFile);

        // 2. 새 CampingZone 엔티티 생성 및 데이터 매핑
        CampingZone newCampingZone = new CampingZone();
        newCampingZone.setAdmin(admin);
        newCampingZone.setName(dto.getName());
        newCampingZone.setDescription(dto.getDescription());
        newCampingZone.setCapacity(dto.getCapacity());
        newCampingZone.setPrice(dto.getPrice());
        newCampingZone.setType(dto.getType());
        newCampingZone.setDefaultSize(dto.getDefaultSize());
        newCampingZone.setFloor(dto.getFloor());
        newCampingZone.setParking(dto.getParking() != null && dto.getParking() == 1);
        newCampingZone.setActive(dto.getIsActive() != null && dto.getIsActive() == 1);

        // 3. S3에서 받은 이미지 URL 설정
        newCampingZone.setImageUrl(imageUrl);

        // 4. DB에 저장
        CampingZone savedCampingZone = campingZoneRepository.save(newCampingZone);
        return CampingZoneDto.from(savedCampingZone);
    }

    // === [신규] React 앱용 (FormData + 파일) ===
    @Transactional
    public CampingZoneDto updateCampingZoneWithForm(AdminEntity admin, Long zoneId, CampingZoneFormDto dto, MultipartFile imageFile) {

        // 1. 엔티티 조회 및 권한 확인
        CampingZone campingZone = campingZoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("CampingZone not found with id: " + zoneId));

        if (!campingZone.getAdmin().getId().equals(admin.getId())) {
            throw new SecurityException("수정할 권한이 없습니다.");
        }

        // 2. 파일 처리 로직
        String newImageUrl = campingZone.getImageUrl(); // 기본값: 기존 이미지 URL 유지

        if (imageFile != null && !imageFile.isEmpty()) {
            // 새 파일이 업로드된 경우
            // 2-1. (선택) S3에서 기존 파일 삭제
            if (campingZone.getImageUrl() != null) {
                fileStorageService.deleteFile(campingZone.getImageUrl());
            }
            // 2-2. S3에 새 파일 저장
            newImageUrl = fileStorageService.storeFile(imageFile);
        }

        // 3. 엔티티 업데이트
        // (CampingZone 엔티티에 update 메서드가 있다고 가정)
        campingZone.update(
                dto.getName(),
                dto.getDescription(),
                dto.getCapacity(),
                dto.getPrice(),
                dto.getType(),
                dto.getDefaultSize(),
                dto.getFloor(),
                dto.getParking() != null && dto.getParking() == 1,
                dto.getIsActive() != null && dto.getIsActive() == 1,
                newImageUrl // S3 URL (새 것이거나 기존 것)
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