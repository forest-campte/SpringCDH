package com.Campmate.DYCampmate.service;



import com.Campmate.DYCampmate.dto.CampingZoneDto;
import com.Campmate.DYCampmate.dto.CampingZoneSaveRequestDto;
import com.Campmate.DYCampmate.dto.CampingZoneUpdateRequestDto;
import com.Campmate.DYCampmate.dto.ZoneHomeViewDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CampingZone;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.repository.CampingZoneRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampingZoneService {

    private final CampingZoneRepository campingZoneRepository;
    private final AdminRepo adminRepository;


    // 로그인 시
    // 관리자 기준 캠핑존 조회
    public List<CampingZoneDto> getZonesForAdmin(AdminEntity admin) {
        return campingZoneRepository.findByAdmin(admin)
                .stream()
                .map(CampingZoneDto::from)
                .collect(Collectors.toList());
    }

    // 전체 캠핑존 조회 (홈 화면용)
    public List<ZoneHomeViewDTO> getAllCampingZonesWithRating() {
        return campingZoneRepository.findAllWithAverageRating();
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
