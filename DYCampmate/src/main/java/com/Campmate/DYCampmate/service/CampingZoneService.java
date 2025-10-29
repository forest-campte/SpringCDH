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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampingZoneService {

    private final CampingZoneRepository campingZoneRepository;
    private final AdminRepo adminRepository;
    private final ReviewRepo reviewRepository;


    // 로그인 시
    // 관리자 기준 캠핑존 조회
    public List<CampingZoneDto> getZonesForAdmin(AdminEntity admin) {
        return campingZoneRepository.findByAdmin(admin)
                .stream()
                .map(CampingZoneDto::from)
                .collect(Collectors.toList());
    }

    // 전체 캠핑존 조회 (홈 화면용)
//    public List<ZoneHomeViewDTO> getAllCampingZonesWithRating() {
//        return campingZoneRepository.findAllWithAverageRating_Original();
//    }
    /**
     * 캠핑존을 관리자(Admin)별로 그룹화하여 조회합니다.
     */
    public List<AdminZoneGroupDTO> getZonesGroupedByAdmin() {

        // 1. Repository에서 Admin 정보가 포함된 Zone 리스트 조회
        List<ZoneAdminRatingDTO> allZones = campingZoneRepository.findAllWithAdminAndAverageRating();

        // 2. Admin ID를 기준으로 맵(Map) 생성 (데이터 그룹화)
        // Key: Admin ID, Value: 해당 Admin에 속한 Zone(DTO) 리스트
        Map<Long, List<ZoneAdminRatingDTO>> zonesGroupedByAdminId = allZones.stream()
                .collect(Collectors.groupingBy(ZoneAdminRatingDTO::adminId));

        // 3. 맵을 최종 응답 DTO(AdminZoneGroupDTO) 리스트로 변환
        return zonesGroupedByAdminId.entrySet().stream()
                .map(entry -> {
                    // (entry.getValue()는 List<ZoneAdminRatingDTO> 임)

                    // 3-1. 이 그룹의 Admin 이름 가져오기 (리스트의 첫 번째 요소에서)
                    String adminName = entry.getValue().get(0).adminName();

                    // 3-2. Zone 리스트를 원본 DTO(ZoneHomeViewDTO) 리스트로 변환
                    List<ZoneHomeViewDTO> sites = entry.getValue().stream()
                            .map(ZoneAdminRatingDTO::toZoneHomeViewDTO) // 헬퍼 메서드 사용
                            .collect(Collectors.toList());

                    // 3-3. 최종 DTO 빌드
                    return AdminZoneGroupDTO.builder()
                            .name(adminName)
                            .sites(sites)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 캠핑존 상세 정보 조회
     */
    public CampsiteDetailDTO getCampsiteDetail(Long campsiteId) {
        CampingZone mainZone = campingZoneRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException("Campsite not found: " + campsiteId));

        AdminEntity admin = mainZone.getAdmin();

        List<CampingZone> allSitesFromAdmin = campingZoneRepository.findAllByAdmin(admin);

        List<CampsiteSiteDTO> siteDTOs = allSitesFromAdmin.stream()
                .map(CampsiteSiteDTO::fromEntity)
                .collect(Collectors.toList());

        Double averageRating = reviewRepository.findAverageRatingByCampingZoneId(campsiteId);

        return CampsiteDetailDTO.fromEntity(mainZone, averageRating, siteDTOs);
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
