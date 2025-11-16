package com.Campmate.DYCampmate.service;



import com.Campmate.DYCampmate.dto.*;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CampingZone;
import com.Campmate.DYCampmate.entity.ReviewEntity;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.repository.CampingZoneRepository;
import com.Campmate.DYCampmate.repository.ReservationRepo;
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
    private final ReservationRepo reservationRepository;

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
     * 캠핑장(Admin) 상세 정보 + 소유한 캠핑존(Zone) 목록 조회
     * @param adminId (id는 이제 adminId로 취급)

     */
    @Transactional(readOnly = true)
    public CampsiteDetailDTO getCampsiteDetail(Long adminId) {
        // 1. ID로 Admin(캠핑장) 정보를 찾습니다.
        AdminEntity admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Campsite (Admin) not found: " + adminId));

        // 2. 해당 Admin ID에 속한 모든 CampingZone(사이트) 목록을 찾습니다.
        //    (CampingZoneRepository에 findByAdminsId 메서드 필요)
        List<CampingZone> zones = campingZoneRepository.findByAdmin_Id(adminId);

        // 3. Entity를 DTO로 변환하여 반환합니다.
        return CampsiteDetailDTO.fromEntity(admin, zones);
    }

    /**
     * 캠핑장(Admin)에 달린 모든 리뷰 조회
     * @param adminId (id는 이제 adminId로 취급)
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviews(Long adminId) {

        // 1. 해당 Admin ID에 속한 모든 CampingZone(사이트) 목록을 찾습니다.
        List<CampingZone> zones = campingZoneRepository.findByAdmin_Id(adminId);

        if (zones.isEmpty()) {
            return Collections.emptyList(); // 존이 없으면 빈 리뷰 목록 반환
        }

        // 2. 모든 CampingZone의 ID 목록을 추출합니다.
        List<Long> zoneIds = zones.stream()
                .map(CampingZone::getId)
                .collect(Collectors.toList());

        // 3. 해당 ID 목록에 포함된 모든 리뷰를 조회합니다.
        //    (ReviewRepository에 findByCampingZoneIdIn 메서드 필요)
        List<ReviewEntity> reviews = reviewRepository.findByCampingZoneIdIn(zoneIds);

        // 4. Entity를 DTO로 변환하여 반환합니다.
        return reviews.stream()
                .map(review -> ReviewDTO.fromEntity(review)) // ReviewDTO::fromEntity 사용
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

    /**
     * === 캠핑존 삭제 (소유권 확인 및 S3 파일 삭제 포함) ===
     *
     * @param adminId 현재 로그인된 관리자 ID
     * @param zoneId 삭제할 캠핑존 ID
     */
    @Transactional // (readOnly = true 아님!)
    public void deleteCampingZone(Long adminId, Long zoneId) {

        // 1. 캠핑존 조회
        CampingZone campingZone = campingZoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("CampingZone not found with id: " + zoneId));

        // 2. 소유권 확인
        if (!campingZone.getAdmin().getId().equals(adminId)) {
            throw new SecurityException("삭제할 권한이 없습니다.");
        }

        // 3. 자식 데이터(리뷰, 예약) 먼저 삭제
        reviewRepository.deleteAllByCampingZone(campingZone);
        reservationRepository.deleteAllByCampingZone(campingZone);

        // 4. S3에 업로드된 이미지 파일 삭제
        if (campingZone.getImageUrl() != null && !campingZone.getImageUrl().isEmpty()) {
            fileStorageService.deleteFile(campingZone.getImageUrl());
        }

        // 5. 캠핑존 DB에서 삭제
        campingZoneRepository.delete(campingZone);
    }


    // 25.11.14 KM 추가: 캠핑장 검색 로직 구현
    @Transactional(readOnly = true)
    public List<CampsiteDetailDTO> searchCampsites(String keyword, String region) {
        // 1. AdminRepo에 구현된 검색 메소드를 호출하여 Admin(캠핑장) 리스트를 가져옵니다.
        List<AdminEntity> admins = adminRepository.searchAdminsByKeywordAndRegion(keyword, region);

        // 2. 검색된 Admin 리스트를 앱 프론트엔드 형식인 CampsiteDetailDTO로 변환합니다.
        return admins.stream()
                .map(admin -> {
                    // 각 Admin에 속한 Zones 목록을 조회하여 DTO 변환 시 사용
                    List<CampingZone> zones = campingZoneRepository.findByAdmin_Id(admin.getId());
                    return CampsiteDetailDTO.fromEntity(admin, zones);
                })
                .collect(Collectors.toList());
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