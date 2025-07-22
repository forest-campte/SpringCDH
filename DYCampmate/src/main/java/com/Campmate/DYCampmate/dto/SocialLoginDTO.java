package com.Campmate.DYCampmate.dto;

import lombok.*;

@Data
public class SocialLoginDTO {
    private String id;
    private String email;
    private String provider; // "KAKAO", "GOOGLE", "NORMAL"
}
