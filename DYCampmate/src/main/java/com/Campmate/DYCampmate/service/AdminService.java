package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.dto.AdminDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepo adminRepository;
    private final PasswordEncoder passwordEncoder;
//    public AdminService(AdminRepo adminRepo) { this.adminRepo = adminRepo;}
    private final CustomerRepo customerRepo;

    //AutoConroller
    public AdminEntity findByEmail(String email) {
        return adminRepository.findByEmail(email).orElse(null);
    }

    //AdminController
    public void register(AdminDTO dto) {
        if (adminRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        System.out.println(dto.getCreateDt());
        AdminEntity admin = AdminEntity.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .description(dto.getDescription())
                .campingStyle(dto.getCampingStyle())
                .campingBackground(dto.getCampingBackground())
                .campingType(dto.getCampingType())
                .createDt(LocalDateTime.now())
                .build();

        adminRepository.save(admin); // 실제 DB 저장
    }

    //맞춤형 캠핑장 리스트 검색
    //CustomerController /customer/admins/{customerId}
    public List<AdminDTO> recommendAdmins(Long customerId) {
        CustomerEntity customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 고객이 없습니다"));

        String style = customer.getCustomersStyle();
        String background = customer.getCustomersBackground();
        String type = customer.getCustomersType();

        List<AdminEntity> admins = adminRepository.findMatchingAdmins(style, background, type);


        return admins.stream()
                .map(admin -> {
                    int score = 0;
                    if (admin.getCampingStyle().contains(style)) score++;
                    if (admin.getCampingBackground().contains(background)) score++;
                    if (admin.getCampingType().contains(type)) score++;

                    return new AbstractMap.SimpleEntry<>(admin, score);
                })
                .filter(entry -> entry.getValue() > 0) // 최소 1개 이상 일치한 것만
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue())) // 점수 높은 순 정렬
                .limit(5) // 상위 5개만
                .map(entry -> AdminDTO.fromEntity(entry.getKey()))
                .toList();
    }





}