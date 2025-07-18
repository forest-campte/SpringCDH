package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.AdminDTO;
import com.Campmate.DYCampmate.dto.AdminRequestDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000") // React 개발 서버 포트에 맞게 설정
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AdminRequestDTO dto) {
        try {
            adminService.register(dto);
            return ResponseEntity.ok("회원가입 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//    public AdminController(AdminService adminService) {
//        this.adminService = adminService;
//    }

//
//    @PostMapping("/register")
//    public ResponseEntity<AdminEntity> register(@RequestBody AdminDTO adminDto) {
//        AdminEntity saved = adminService.registerAdmin(adminDto);
//        return ResponseEntity.ok(saved);
//    }
//
//    @GetMapping("/{email}")
//    public ResponseEntity<AdminEntity> getByEmail(@PathVariable String email) {
//        return adminService.findByEmail(email)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
}
