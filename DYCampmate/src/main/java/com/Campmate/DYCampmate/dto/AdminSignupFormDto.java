package com.Campmate.DYCampmate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 관리자 회원가입 폼 데이터(텍스트)를 받기 위한 DTO
 * (@ModelAttribute 바인딩을 위해 @Data 또는 @Setter 필요)
 */
@Data
@NoArgsConstructor
public class AdminSignupFormDto {

    @NotEmpty(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수입니다.")
    private String password;

    @NotEmpty(message = "이름은 필수입니다.")
    private String name;

    private String description;

    @NotEmpty(message = "캠핑 스타일은 필수입니다.")
    private String campingStyle;

    @NotEmpty(message = "캠핑 배경은 필수입니다.")
    private String campingBackground;

    @NotEmpty(message = "캠핑 타입은 필수입니다.")
    private String campingType;

    private String address;

    // imageFile은 @RequestParam으로 따로 받습니다.
    // createDt는 서버에서 생성합니다.
}