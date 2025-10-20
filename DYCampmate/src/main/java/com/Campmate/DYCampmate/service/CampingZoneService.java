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
    public List<CampingZoneDto> getZonesForAdmin(Long adminId) {
        return campingZoneRepository.findAllByAdmin_Id(adminId)
                .stream()
                .map(CampingZoneDto::from)
                .collect(Collectors.toList());
    }

    // 전체 캠핑존 조회 (홈 화면용)
    public List<ZoneHomeViewDTO> getAllCampingZones() {
        return campingZoneRepository.findAllWithAverageRating();
    }

    // 캠핑존 생성
    @Transactional
    public CampingZoneDto createCampingZone(CampingZoneSaveRequestDto requestDto) {
        AdminEntity admin = adminRepository.findById(requestDto.adminId())
                .orElseThrow(() -> new EntityNotFoundException("Admin not found with id: " + requestDto.adminId()));

        CampingZone newCampingZone = requestDto.toEntity(admin);
        CampingZone savedCampingZone = campingZoneRepository.save(newCampingZone);
        return CampingZoneDto.from(savedCampingZone);
    }

    // 캠핑존 수정
    @Transactional
    public CampingZoneDto updateCampingZone(Long zoneId, CampingZoneUpdateRequestDto requestDto) {
        CampingZone campingZone = campingZoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("CampingZone not found with id: " + zoneId));

        campingZone.update(
                requestDto.name(),
                requestDto.description(),
                requestDto.capacity(),
                requestDto.price(),
                requestDto.type(),
                requestDto.defaultSize(),
                requestDto.floor(),
                requestDto.parking() == 1,
                requestDto.isActive() == 1
        );
        // campingZone은 영속성 컨텍스트에 의해 관리되므로,
        // @Transactional 어노테이션 덕분에 메서드 종료 시 변경 감지(dirty checking)가 일어나 DB에 자동으로 반영됩니다.
        // 따라서 save()를 명시적으로 호출할 필요가 없습니다.

        return CampingZoneDto.from(campingZone);
    }
}
