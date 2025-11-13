package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.CampingZone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampsiteSiteDTO {
    // 프론트엔드 makeReservation의 site.siteId와 일치
    private Long siteId;
    private String name;
    private String description;
    private String imageUrl;
    private Integer capacity;
    private Integer price;
    private String type;
    private String floor;
    private Boolean parking; // BIT(1)은 Boolean으로 매핑
    private Boolean isActive;

    // Entity -> DTO 변환을 위한 정적 팩토리 메서드
    public static CampsiteSiteDTO fromEntity(CampingZone entity) {
        return new CampsiteSiteDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getImageUrl(),
                entity.getCapacity(),
                entity.getPrice(),
                entity.getType(),
                entity.getFloor(),
                entity.isParking(),
                entity.isActive()
        );
    }
}