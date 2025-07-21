package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.LoginRequestDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admins")
//요청 처리 + 응답 반환
//RequestDTO를 받아서 Service 호출 → ResponseDTO로 응답
public class AuthController {

    private final AdminService adminService;

    public AuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDto) {
        AdminEntity admin = adminService.findByEmail(loginDto.getEmail());

        if (admin != null && admin.getPassword().equals(loginDto.getPassword())) {
            admin.setPassword(null); // 비밀번호는 프론트로 전달 X
            return ResponseEntity.ok(admin);
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "이메일 또는 비밀번호가 올바르지 않습니다."));
        }
    }




}