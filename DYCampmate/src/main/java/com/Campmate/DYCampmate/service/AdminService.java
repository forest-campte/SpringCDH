// /src/main/java/com/Campmate/DYCampmate/service/AdminService.java

package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.dto.AdminDTO;
import com.Campmate.DYCampmate.dto.AdminUpdateRequestDto;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepo adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepo customerRepo;

    // (FileStorageService.java 파일이 프로젝트 내에 존재해야 합니다)
    private final FileStorageService fileStorageService;

    //AutoConroller
    public AdminEntity findByEmail(String email) {
        return adminRepository.findByEmail(email).orElse(null);
    }

    //AdminController
    @Transactional
    public void register(AdminDTO dto, MultipartFile imageFile) {
        if (adminRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        // 1. S3에 이미지 파일 저장 (파일 없으면 null 반환)
        String imageUrl = fileStorageService.storeFile(imageFile);

        AdminEntity admin = AdminEntity.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .description(dto.getDescription())
                .campingStyle(dto.getCampingStyle())
                .campingBackground(dto.getCampingBackground())
                .campingType(dto.getCampingType())
                .address(dto.getAddress()) // 폼에서 전송된 주소 문자열
                .imageUrl(imageUrl)
                .createDt(LocalDateTime.now())
                .build();

        adminRepository.save(admin); // 실제 DB 저장
    }

    // 관리자 정보 수정 (기존 JSON용)
    @Transactional
    public AdminEntity updateAdmin(Long adminId, AdminUpdateRequestDto updateDto) {
        AdminEntity admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("해당 관리자를 찾을 수 없습니다: " + adminId));

        admin.update(
                updateDto.email(),
                updateDto.name(),
                updateDto.description(),
                updateDto.campingStyle(),
                updateDto.campingBackground(),
                updateDto.campingType(),
                updateDto.address(),
                updateDto.imageUrl()
        );
        return admin;
    }

    // --- [신규 추가] 계정 정보 수정 (FormData + 파일 처리) ---
    /**
     * Admin 계정 정보를 FormData(파일 포함)로 수정합니다.
     */
    @Transactional
    public AdminEntity updateAdminWithForm(Long adminId, AdminDTO dto, MultipartFile imageFile) {

        // 1. 엔티티 조회
        AdminEntity admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("해당 관리자를 찾을 수 없습니다: " + adminId));

        // 2. 파일 처리 로직
        String newImageUrl = admin.getImageUrl(); // 기본값: 기존 이미지 URL 유지

        if (imageFile != null && !imageFile.isEmpty()) {
            // 새 파일이 업로드된 경우
            // 2-1. (선택) S3에서 기존 파일 삭제
            if (admin.getImageUrl() != null) {
                fileStorageService.deleteFile(admin.getImageUrl());
            }
            // 2-2. S3에 새 파일 저장
            newImageUrl = fileStorageService.storeFile(imageFile);
        }

        // 3. 엔티티 업데이트 (AdminEntity의 update 메서드 사용)
        admin.update(
                dto.getEmail(),
                dto.getName(),
                dto.getDescription(),
                dto.getCampingStyle(),
                dto.getCampingBackground(),
                dto.getCampingType(),
                dto.getAddress(),
                newImageUrl // S3 URL (새 것이거나 기존 것)
        );

        // 4. DB에 저장 (변경 감지로 인해 save 호출은 생략 가능하나 명시적 반환)
        return admin;
    }
    // -----------------------------------------------------------


    //맞춤형 캠핑장 리스트 검색
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