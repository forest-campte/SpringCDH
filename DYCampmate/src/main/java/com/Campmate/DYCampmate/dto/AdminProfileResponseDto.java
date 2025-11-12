package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.AdminEntity;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 관리자 정보를 반환하기 위한 DTO (password 제외)
 */
@Builder
public record AdminProfileResponseDto(
        Long id,
        String email,
        String name,
        String address,
        String imageUrl,
        String description,
        String campingStyle,
        String campingBackground,
        String campingType,
        LocalDateTime createDt,
        String phoneNumber
) {
    // Entity를 Response DTO로 변환하는 정적 팩토리 메소드
    public static AdminProfileResponseDto fromEntity(AdminEntity entity) {
        return AdminProfileResponseDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .address(entity.getAddress())
                .imageUrl(entity.getImageUrl())
                .description(entity.getDescription())
                .campingStyle(entity.getCampingStyle())
                .campingBackground(entity.getCampingBackground())
                .campingType(entity.getCampingType())
                .createDt(entity.getCreateDt())
                .phoneNumber(entity.getPhoneNumber())
                .build();
    }
}