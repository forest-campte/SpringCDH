package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.*;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.service.CampingZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/zones")
@CrossOrigin(origins = "http://localhost:3000") // 관리자 웹 도메인
public class CampingZoneController {

    private final CampingZoneService campingZoneService;
    private final AdminRepo adminRepository;

//    @GetMapping("home")
//    public ResponseEntity<List<ZoneHomeViewDTO>> getHomeZones() {
//        List<ZoneHomeViewDTO> zones = campingZoneService.getAllCampingZonesWithRating();
//        return ResponseEntity.ok(zones);
//    }
    // 모든 캠핑존 조회 (GET /api/zones/home) : 앱 홈화면에 캠핑장 리스트
    @GetMapping("home")
    public ResponseEntity<List<AdminZoneGroupDTO>> getHomeZones() {
        List<AdminZoneGroupDTO> zonesByAdmin = campingZoneService.getZonesGroupedByAdmin();
        return ResponseEntity.ok(zonesByAdmin);
    }

    // 앱 홈화면 > 상세정보
    /**
     * 캠핑존 상세 정보 API
     */
    @GetMapping("/{id}")
    public ResponseEntity<CampsiteDetailDTO> getCampsiteDetail(@PathVariable Long id) {
        CampsiteDetailDTO detail = campingZoneService.getCampsiteDetail(id);
        return ResponseEntity.ok(detail);
    }

    /**
     * 캠핑존 리뷰 목록 API
     */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getCampsiteReviews(@PathVariable Long id) {
        List<ReviewDTO> reviews = campingZoneService.getReviews(id);
        return ResponseEntity.ok(reviews);
    }

    // 25.11.14 KM 추가: 캠핑장 검색 API
    @GetMapping("/search")
    public ResponseEntity<List<CampsiteDetailDTO>> searchCampsites(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String region
    ) {
        List<CampsiteDetailDTO> results = campingZoneService.searchCampsites(keyword, region);
        return ResponseEntity.ok(results);
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

    // === React용 (FormData + 파일) ===
    @PostMapping(value = "/form-data", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CampingZoneDto> createZoneWithForm(
            @ModelAttribute CampingZoneFormDto formDto, // ◀ 폼 DTO
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile // ◀ 파일
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = Long.parseLong(authentication.getName());
        AdminEntity currentAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with ID: " + adminId));

        // 새 서비스 메서드 호출
        CampingZoneDto createdZone = campingZoneService.createCampingZoneWithForm(currentAdmin, formDto, imageFile);
        return new ResponseEntity<>(createdZone, HttpStatus.CREATED);
    }

    // === React용 (FormData + 파일) ===
    @PutMapping(value = "/{id}/form-data", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CampingZoneDto> updateZoneWithForm(
            @PathVariable Long id,
            @ModelAttribute CampingZoneFormDto formDto, // ◀ 폼 DTO
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile // ◀ 파일
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = Long.parseLong(authentication.getName());
        AdminEntity currentAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with ID: " + adminId));

        // 새 서비스 메서드 호출
        CampingZoneDto updatedZone = campingZoneService.updateCampingZoneWithForm(currentAdmin, id, formDto, imageFile);
        return ResponseEntity.ok(updatedZone);
    }

    /**
     * === 캠핑존 삭제 (DELETE /api/zones/{id}) ===
     * * @param id 삭제할 캠핑존의 ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {

        // 1. 현재 인증된 Admin의 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = Long.parseLong(authentication.getName());

        // 2. 서비스 레이어에 삭제 작업 위임 (adminId로 소유권 확인)
        campingZoneService.deleteCampingZone(adminId, id);

        // 3. 성공 시 204 No Content 반환
        return ResponseEntity.noContent().build();
    }
}