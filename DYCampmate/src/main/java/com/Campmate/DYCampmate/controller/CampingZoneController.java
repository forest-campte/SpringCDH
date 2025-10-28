package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.CampingZoneDto;
import com.Campmate.DYCampmate.dto.CampingZoneSaveRequestDto;
import com.Campmate.DYCampmate.dto.CampingZoneUpdateRequestDto;
import com.Campmate.DYCampmate.dto.ZoneHomeViewDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.service.CampingZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/zones")
@CrossOrigin(origins = "http://localhost:3000") // 관리자 웹 도메인
public class CampingZoneController {

    private final CampingZoneService campingZoneService;
    private final AdminRepo adminRepository;

    // 모든 캠핑존 조회 (GET /api/zones/home)
    @GetMapping("home")
    public ResponseEntity<List<ZoneHomeViewDTO>> getHomeZones() {
        List<ZoneHomeViewDTO> zones = campingZoneService.getAllCampingZonesWithRating();
        return ResponseEntity.ok(zones);
    }
    @GetMapping("/all")
    public ResponseEntity<List<CampingZoneDto>> getAllZones() { // 반환 타입을 CampingZoneDto로 수정
        List<CampingZoneDto> zones = campingZoneService.getAllCampingZones();
        return ResponseEntity.ok(zones);
    }

    // 로그인한 유저의 모든 캠핑존 조회 (GET /api/zones)
    @GetMapping
    public ResponseEntity<List<CampingZoneDto>> getMyZones() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long adminId = Long.parseLong(authentication.getName());

        AdminEntity currentAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with ID: " + adminId));

        // --- [핵심 수정] ---
        // 서비스 메서드에 AdminEntity 객체 전달
        List<CampingZoneDto> zones = campingZoneService.getZonesForAdmin(currentAdmin);
        // -----------------------
        return ResponseEntity.ok(zones);
    }

    // 새 캠핑존 추가 (POST /api/zones)
    @PostMapping
    public ResponseEntity<CampingZoneDto> createZone(@RequestBody CampingZoneSaveRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long adminId = Long.parseLong(authentication.getName());

        AdminEntity currentAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with ID: " + adminId));

        // --- [핵심 수정] ---
        // 서비스 메서드에 AdminEntity와 DTO를 함께 전달
        CampingZoneDto createdZone = campingZoneService.createCampingZone(currentAdmin, requestDto);
        // -----------------------
        return new ResponseEntity<>(createdZone, HttpStatus.CREATED);
    }

    // 캠핑존 수정 (PUT /api/zones/{id})
    @PutMapping("/{id}")
    public ResponseEntity<CampingZoneDto> updateZone(
            @PathVariable Long id,
            @RequestBody CampingZoneUpdateRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long adminId = Long.parseLong(authentication.getName());

        AdminEntity currentAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with ID: " + adminId));

        // --- [핵심 수정] ---
        // 서비스 메서드에 AdminEntity, zoneId, DTO를 함께 전달
        CampingZoneDto updatedZone = campingZoneService.updateCampingZone(currentAdmin, id, requestDto);
        // -----------------------
        return ResponseEntity.ok(updatedZone);
    }
}