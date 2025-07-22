package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.JwtUtil;
import com.Campmate.DYCampmate.dto.LoginRequestDTO;
import com.Campmate.DYCampmate.dto.SocialLoginDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import com.Campmate.DYCampmate.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
//요청 처리 + 응답 반환
//RequestDTO를 받아서 Service 호출 → ResponseDTO로 응답
public class AuthController {

    private final AdminService adminService;
    private final CustomerRepo customerRepository;
    private final JwtUtil jwtUtil;

    public PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @PostMapping("/admins/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDto) {
        AdminEntity admin = adminService.findByEmail(loginDto.getEmail().trim());
        System.out.println("검색된 관리자: " + admin);
        if (admin != null && passwordEncoder.matches(loginDto.getPassword(), admin.getPassword())){
            admin.setPassword(null); // 비밀번호는 프론트로 전달 X
            return ResponseEntity.ok(admin);
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "이메일 또는 비밀번호가 올바르지 않습니다."));
        }
    }


//    {
//            "id": "123456789",              // 소셜 고유 ID (카카오: id, 구글: sub)
//            "email": "user@kakao.com",      // 사용자 이메일
//            "provider": "KAKAO"             // "KAKAO" 또는 "GOOGLE"
//    }
    @PostMapping("/customer/social")
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginDTO dto) {
        String socialId = dto.getId();
        String provider = dto.getProvider(); // "KAKAO", "GOOGLE", "NORMAL"

        CustomerEntity user = customerRepository
                .findByCustomerIdAndProvider(socialId, provider)
                .orElse(null);

        // 소셜 회원가입 시 기본 비밀번호 부여
        String dummyPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        if (user == null) {
            user = CustomerEntity.builder()
                    .customerId(socialId)
                    .provider(provider)
                    .email(dto.getEmail())
                    .password(dummyPassword)
                    .customersStyle("-")
                    .customersBackground("-")
                    .customersType("-")
                    .createdDate(LocalDateTime.now())
                    .build();

            customerRepository.save(user);
        }

        // (선택) JWT 발급
        String jwt = jwtUtil.createToken(user.getCustomerId(), user.getEmail());

        return ResponseEntity.ok(Map.of(
                "message", "로그인 성공",
                "token", jwt
        ));
    }


}