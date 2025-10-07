package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.CampingZoneDto;
import com.Campmate.DYCampmate.dto.CampingZoneSaveRequestDto;
import com.Campmate.DYCampmate.dto.CampingZoneUpdateRequestDto;
import com.Campmate.DYCampmate.service.CampingZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/zones")
@CrossOrigin(origins = "http://localhost:3000") // 프론트엔드 개발 서버의 주소
public class CampingZoneController {

    private final CampingZoneService campingZoneService;

    // 모든 캠핑존 조회 (GET /api/zones/all)
    @GetMapping("all")
    public ResponseEntity<List<CampingZoneDto>> getAllZones() {
        List<CampingZoneDto> zones = campingZoneService.getAllCampingZones();
        return ResponseEntity.ok(zones);
    }

    // 로그인한 유저의 모든 캠핑존 조회 (GET /api/zones)
    @GetMapping
    public ResponseEntity<List<CampingZoneDto>> getMyZones(@AuthenticationPrincipal User user) {
        Long adminId = Long.parseLong(user.getUsername());

        return ResponseEntity.ok(campingZoneService.getZonesForAdmin(adminId));
    }

    // 새 캠핑존 추가 (POST /api/zones)
    @PostMapping
    public ResponseEntity<CampingZoneDto> createZone(@RequestBody CampingZoneSaveRequestDto requestDto) {
        CampingZoneDto createdZone = campingZoneService.createCampingZone(requestDto);
        return new ResponseEntity<>(createdZone, HttpStatus.CREATED);
    }

    // 캠핑존 수정 (PUT /api/zones/{id})
    @PutMapping("/{id}")
    public ResponseEntity<CampingZoneDto> updateZone(
            @PathVariable Long id,
            @RequestBody CampingZoneUpdateRequestDto requestDto) {
        CampingZoneDto updatedZone = campingZoneService.updateCampingZone(id, requestDto);
        return ResponseEntity.ok(updatedZone);
    }
}