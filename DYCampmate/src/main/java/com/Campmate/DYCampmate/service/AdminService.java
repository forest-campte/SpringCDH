package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.dto.AdminDTO;
import com.Campmate.DYCampmate.dto.AdminRequestDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepo adminRepository;
    private final PasswordEncoder passwordEncoder;
//    public AdminService(AdminRepo adminRepo) { this.adminRepo = adminRepo;}
    private final CustomerRepo customerRepo;

    public AdminEntity findByEmail(String email) {
        return adminRepository.findByEmail(email).orElse(null);
    }
    public void register(AdminRequestDTO dto) {
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
    public List<AdminDTO> recommendAdmins(Long customerId) {
        CustomerEntity customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 고객이 없습니다"));

        String style = customer.getCustomersStyle();
        String background = customer.getCustomersBackground();
        String type = customer.getCustomersType();

        List<AdminEntity> admins = adminRepository.findMatchingAdmins(style, background, type);


        return admins.stream()
                .limit(5)
                .map(AdminDTO::fromEntity)
                .toList();
    }





}