package com.Campmate.DYCampmate.dto;


import com.Campmate.DYCampmate.entity.CampingZone;
import lombok.Builder;

@Builder
public record CampingZoneDto(
        Long id,
        String name,
        String description,
        String imageUrl,
        int capacity,
        int price,
        String type,
        String defaultSize,
        String floor,
        boolean parking,
        boolean isActive
) {
    // Entity -> DTO 변환을 위한 정적 팩토리 메서드
    public static CampingZoneDto from(CampingZone entity) {
        return CampingZoneDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .capacity(entity.getCapacity())
                .price(entity.getPrice())
                .type(entity.getType())
                .defaultSize(entity.getDefaultSize())
                .floor(entity.getFloor())
                .parking(entity.isParking())
                .isActive(entity.isActive())
                .build();
    }
}
