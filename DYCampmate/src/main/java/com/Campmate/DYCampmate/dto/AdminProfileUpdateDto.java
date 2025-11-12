package com.Campmate.DYCampmate.dto;

import jakarta.validation.constraints.NotEmpty;

/**
 * 관리자 프로필 수정을 위한 DTO
 * (email, password 제외)
 */
public record AdminProfileUpdateDto(
        @NotEmpty(message = "이름은 필수입니다.")
        String name,
        String address,
        String imageUrl,
        String description,

        @NotEmpty(message = "캠핑 스타일은 필수입니다.")
        String campingStyle,

        @NotEmpty(message = "캠핑 배경은 필수입니다.")
        String campingBackground,

        @NotEmpty(message = "캠핑 타입은 필수입니다.")
        String campingType,

        String phoneNumber
) {
}