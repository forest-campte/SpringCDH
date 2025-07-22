package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.AdminDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000") // config/WebConfig 통합 설정
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AdminDTO dto) {
        try {
            adminService.register(dto);
            return ResponseEntity.ok("회원가입 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//    public AdminController(AdminService adminService) {
//        this.adminService = adminService;
//    }@RequiredArgsConstructor Annotation 으로 대체



}
