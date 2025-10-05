package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.ZoneDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService campingZoneService;

    // 관리자별 캠핑존 조회
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<ZoneDTO>> getZonesByAdmin(@PathVariable Long adminId) {
        AdminEntity admin = AdminEntity.builder().id(adminId).build(); // adminId로 임시 매핑
        List<ZoneDTO> zones = campingZoneService.getZonesForAdmin(admin);
        return ResponseEntity.ok(zones);
    }

    // 캠핑존 등록
    @PostMapping("/admin/{adminId}")
    public ResponseEntity<ZoneDTO> createZone(
            @PathVariable Long adminId,
            @RequestBody ZoneDTO dto) {
        AdminEntity admin = AdminEntity.builder().id(adminId).build();
        ZoneDTO created = campingZoneService.createZone(admin, dto);
        return ResponseEntity.ok(created);
    }

    // 캠핑존 삭제
    @DeleteMapping("/{zoneId}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long zoneId) {
        campingZoneService.deleteZone(zoneId);
        return ResponseEntity.noContent().build();
    }
}
