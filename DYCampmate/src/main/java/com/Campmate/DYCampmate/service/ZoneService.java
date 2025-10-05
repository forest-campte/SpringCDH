package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.dto.CampingZoneRequest;
import com.Campmate.DYCampmate.dto.CampingZoneResponse;
import com.Campmate.DYCampmate.dto.ZoneDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.ZoneEntity;
import com.Campmate.DYCampmate.repository.ZoneRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneService {
    private final ZoneRepo zoneRepo;

    public ZoneService(ZoneRepo zoneRepo) {this.zoneRepo = zoneRepo;}


    // 관리자 기준 캠핑존 조회
    public List<ZoneDTO> getZonesForAdmin(AdminEntity admin) {
        return zoneRepo.findByAdmin(admin)
                .stream()
                .map(ZoneDTO::new)
                .collect(Collectors.toList());
    }

    // 캠핑존 생성
    public CampingZoneResponse createZone(CampingZoneRequest request) {
        ZoneEntity zone = ZoneEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .price(request.getPrice())
                .type(request.getType())
                .defaultSize(request.getDefault_size())
                .floor(request.getFloor())
                .parking(request.isParking())
                .active(request.isActive())
                .build();

        ZoneEntity saved = zoneRepo.save(zone);
        return CampingZoneResponse.fromEntity(saved);
    }

    // 캠핑존 삭제
    public void deleteZone(Long zoneId) {
        zoneRepo.deleteById(zoneId);
    }

    // 캠핑존 수정
    /** 캠핑존 수정 */
    public CampingZoneResponse updateZone(Long id, CampingZoneRequest request) {
        ZoneEntity zone = zoneRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("캠핑존을 찾을 수 없습니다."));

        zone.setName(request.getName());
        zone.setDescription(request.getDescription());
        zone.setCapacity(request.getCapacity());
        zone.setPrice(request.getPrice());
        zone.setType(request.getType());
        zone.setDefaultSize(request.getDefault_size());
        zone.setFloor(request.getFloor());
        zone.setParking(request.isParking());
        zone.setActive(request.isActive());

        ZoneEntity updated = zoneRepo.save(zone);
        return CampingZoneResponse.fromEntity(updated);
    }

//    public List<ZoneDTO> getZonesForAdmin(AdminEntity admin){
//        return zoneRepo.findByAdmin(admin)
//                .stream()
//                .map(ZoneDTO::new)
//                .collect(Collectors.toList());
//    }

}
