package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.dto.*;
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
    private final FileStorageService fileStorageService;

    //AutoConroller
    public AdminEntity findByEmail(String email) {
        return adminRepository.findByEmail(email).orElse(null);
    }

    /**
     * === [수정] 관리자 회원가입 (S3 업로드 포함) ===
     * @param dto 폼 텍스트 데이터 DTO
     * @param imageFile S3에 업로드할 프로필 이미지 파일
     */
    @Transactional
    public void register(AdminSignupFormDto dto, MultipartFile imageFile) {
        // 1. 이메일 중복 검사
        if (adminRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        // 2. S3에 이미지 파일 업로드 (파일이 없으면 null 반환)
        String s3ImageUrl = fileStorageService.storeFile(imageFile);

        // 3. AdminEntity 생성
        AdminEntity admin = AdminEntity.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .description(dto.getDescription())
                .campingStyle(dto.getCampingStyle())
                .campingBackground(dto.getCampingBackground())
                .campingType(dto.getCampingType())
                .address(dto.getAddress())
                .imageUrl(s3ImageUrl) // ◀ S3에서 반환된 URL 저장
                .createDt(LocalDateTime.now())
                .build();

        // 4. DB에 저장
        adminRepository.save(admin);
    }

    // 관리자 정보 수정
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

    /**
     * 관리자 프로필 정보 수정
     * @param adminId 현재 로그인된 관리자의 ID
     * @param dto 수정할 정보가 담긴 DTO
     * @return 수정된 정보가 담긴 DTO
     */
    @Transactional
    public AdminProfileResponseDto updateAdminProfile(Long adminId, AdminProfileUpdateDto dto) {

        // 1. 현재 관리자 엔티티 조회
        AdminEntity admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found with id: " + adminId));

        // 2. DTO의 값으로 엔티티 필드 업데이트 (JPA의 'Dirty Checking' 활용)
        // (email과 password는 건드리지 않음)
        admin.setName(dto.name());
        admin.setAddress(dto.address());
        admin.setImageUrl(dto.imageUrl());
        admin.setDescription(dto.description());
        admin.setCampingStyle(dto.campingStyle());
        admin.setCampingBackground(dto.campingBackground());
        admin.setCampingType(dto.campingType());
        admin.setPhoneNumber(dto.phoneNumber());

        // 3. @Transactional 어노테이션에 의해 트랜잭션 종료 시
        //    변경 감지(Dirty Checking)가 동작하여 자동으로 UPDATE 쿼리가 실행됨.
        //    (명시적으로 save 호출도 가능: adminRepository.save(admin);)

        // 4. 변경된 엔티티를 Response DTO로 변환하여 반환
        return AdminProfileResponseDto.fromEntity(admin);
    }

    @Transactional(readOnly = true)
    public AdminProfileResponseDto getAdminProfile(Long adminId) {
        AdminEntity admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found with id: " + adminId));

        return AdminProfileResponseDto.fromEntity(admin);
    }


}