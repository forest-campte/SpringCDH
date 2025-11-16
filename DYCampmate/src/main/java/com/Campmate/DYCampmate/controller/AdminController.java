// /src/main/java/com/Campmate/DYCampmate/controller/AdminController.java

package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.AdminDTO;
import com.Campmate.DYCampmate.dto.AdminResponseDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000") // config/WebConfig 통합 설정
public class AdminController {
    private final AdminService adminService;
    private final AdminRepo adminRepository; // AdminRepo 주입

    /**
     * 회원가입 (FormData + 이미지 파일 처리)
     */
    @PostMapping(value = "/signup", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> signup(
            @ModelAttribute AdminDTO dto, // @RequestBody -> @ModelAttribute
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile // imageFile 파라미터 추가
    ) {
        try {
            // 서비스에도 imageFile 전달
            adminService.register(dto, imageFile);
            return ResponseEntity.ok("회원가입 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 현재 로그인된 관리자(Admin)의 정보를 반환합니다.
     * React 앱이 새로고침될 때 자동 로그인을 위해 사용됩니다.
     */
    @GetMapping("/me")
    public ResponseEntity<AdminResponseDTO> getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long adminId = Long.parseLong(authentication.getName());

        AdminEntity currentAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with ID: " + adminId));

        AdminResponseDTO userDto = new AdminResponseDTO(currentAdmin);
        return ResponseEntity.ok(userDto);
    }

    // --- [신규 추가] 계정 정보 수정 (FormData + 파일 처리) ---
    /**
     * 현재 로그인된 관리자(Admin)의 정보를 수정합니다.
     * React의 AdminPage.js에서 FormData로 전송하는 요청을 처리합니다.
     */
    @PutMapping(value = "/me", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<AdminResponseDTO> updateMyInfo(
            @ModelAttribute AdminDTO dto, // @ModelAttribute로 텍스트 데이터 받기
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile // @RequestParam으로 파일 받기
    ) {
        // 1. SecurityContext에서 현재 사용자 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long adminId = Long.parseLong(authentication.getName());

        // 2. 서비스 레이어 호출
        AdminEntity updatedAdmin = adminService.updateAdminWithForm(adminId, dto, imageFile);

        // 3. 프론트엔드(AdminPage.js)가 setUser로 사용할 수 있도록 DTO로 변환하여 반환
        return ResponseEntity.ok(new AdminResponseDTO(updatedAdmin));
    }
    // -----------------------------------------------------------

    /**
     * '전체 관리자' 페이지를 위해 모든 관리자 계정 목록을 반환합니다.
     */
    @GetMapping("/all")
    public ResponseEntity<List<AdminDTO>> getAllAdmins() {
        List<AdminEntity> allAdmins = adminRepository.findAll();

        List<AdminDTO> dtos = allAdmins.stream()
                .map(AdminDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}