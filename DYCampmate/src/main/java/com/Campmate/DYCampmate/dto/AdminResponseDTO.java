package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.AdminEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminResponseDTO {

    private Long id;
    private String email;
    private String name;

    // 캠핑장 설명/스타일 정보
    private String description;
    private String campingStyle;
    private String campingBackground;
    private String campingType;

    // 주소/이미지
    private String address;
    private String imageUrl;

    // 계정 생성일
    private LocalDateTime createDt;

    public AdminResponseDTO(AdminEntity admin) {
        this.id = admin.getId();
        this.email = admin.getEmail();
        this.name = admin.getName();

        this.description = admin.getDescription();
        this.campingStyle = admin.getCampingStyle();
        this.campingBackground = admin.getCampingBackground();
        this.campingType = admin.getCampingType();

        this.address = admin.getAddress();
        this.imageUrl = admin.getImageUrl();

        this.createDt = admin.getCreateDt();
    }
}
