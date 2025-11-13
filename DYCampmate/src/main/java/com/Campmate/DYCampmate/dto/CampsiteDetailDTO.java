package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CampingZone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampsiteDetailDTO {
    // 프론트엔드 makeReservation의 currentCampsite.adminId와 일치
    private Long adminId;
    private String name;
    private String description;
    private String imageUrl;
    private String address;
    private String campingStyle;
    private String campingBackground;
    private String campingType;

    // 프론트엔드 campsite.sites와 일치
    private List<CampsiteSiteDTO> sites;

    // Entity -> DTO 변환을 위한 정적 팩토리 메서드
    public static CampsiteDetailDTO fromEntity(AdminEntity admin, List<CampingZone> zones) {
        List<CampsiteSiteDTO> siteDTOs = zones.stream()
                .map(CampsiteSiteDTO::fromEntity)
                .collect(Collectors.toList());

        return new CampsiteDetailDTO(
                admin.getId(),
                admin.getName(),
                admin.getDescription(),
                admin.getImageUrl(),
                admin.getAddress(),
                admin.getCampingStyle(),
                admin.getCampingBackground(),
                admin.getCampingType(),
                siteDTOs
        );
    }
}