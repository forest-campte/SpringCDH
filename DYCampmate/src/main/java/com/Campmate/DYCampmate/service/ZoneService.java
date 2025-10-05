package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.dto.ZoneDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.ZoneEntity;
import com.Campmate.DYCampmate.repository.ZoneRepo;
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
    public ZoneDTO createZone(AdminEntity admin, ZoneDTO dto) {
        ZoneEntity entity = ZoneEntity.builder()
                .admin(admin)
                .name(dto.getName())
                .description(dto.getDescription())
                .capacity(dto.getCapacity())
                .price(dto.getPrice())
                .type(dto.getType())
                .defaultSize(dto.getDefaultSize())
                .floor(dto.getFloor())
                .parking(dto.getParking())
                .isActive(dto.getIsActive())
                .build();

        ZoneEntity saved = zoneRepo.save(entity);
        return new ZoneDTO(saved);
    }

    // 캠핑존 삭제
    public void deleteZone(Long zoneId) {
        zoneRepo.deleteById(zoneId);
    }

    // 캠핑존 수정
    public ZoneDTO updateZone(Long zoneId, ZoneDTO dto) {
        ZoneEntity entity = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 캠핑존입니다... "+ zoneId));

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCapacity(dto.getCapacity());
        entity.setPrice(dto.getPrice());
        entity.setType(dto.getType());
        entity.setDefaultSize(dto.getDefaultSize());
        entity.setFloor(dto.getFloor());
        entity.setParking(dto.getParking());
        entity.setIsActive(dto.getIsActive());

        ZoneEntity updated = zoneRepo.save(entity);

        return new ZoneDTO(updated);
    }

//    public List<ZoneDTO> getZonesForAdmin(AdminEntity admin){
//        return zoneRepo.findByAdmin(admin)
//                .stream()
//                .map(ZoneDTO::new)
//                .collect(Collectors.toList());
//    }

}
