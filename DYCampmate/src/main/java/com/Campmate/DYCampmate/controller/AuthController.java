package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.JwtUtil;
import com.Campmate.DYCampmate.dto.*;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import com.Campmate.DYCampmate.service.AdminService;
import com.Campmate.DYCampmate.service.AuthService;
import com.Campmate.DYCampmate.service.CampingZoneService;
import com.Campmate.DYCampmate.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
//요청 처리 + 응답 반환
//RequestDTO를 받아서 Service 호출 → ResponseDTO로 응답
public class AuthController {

    private final AdminService adminService;
    private final AuthService authService;
    private final ReservationService reservationService;
    private final CustomerRepo customerRepository;
    private final JwtUtil jwtUtil;
    private final CampingZoneService campingZoneService;
    private final PasswordEncoder passwordEncoder;
//    public PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();



    @PostMapping("/admins/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {

        AdminEntity admin = authService.authenticate(request);

        String token = jwtUtil.createToken(String.valueOf(admin.getId()),admin.getEmail());

        AdminResponseDTO user = new AdminResponseDTO(admin);
        List<ReservationDTO> reservations = reservationService.getReservationsForAdmin(admin);
        List<CampingZoneDto> zones = campingZoneService.getZonesForAdmin(admin);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("reservations", reservations);
        response.put("zones", zones);
        response.put("token", token);

        return ResponseEntity.ok(response);
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