package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.CampingZone;
import lombok.Builder;

// 상세 페이지 하단의 '사이트 목록'에 들어갈 개별 DTO
@Builder
public record CampsiteSiteDTO(
        Long siteId, // camping_zones.id
        String name, // camping_zones.name
        Integer price // camping_zones.price
) {
    // CampingZone 엔티티를 이 DTO로 변환하는 정적 메서드
    public static CampsiteSiteDTO fromEntity(CampingZone zone) {
        return CampsiteSiteDTO.builder()
                .siteId(zone.getId())
                .name(zone.getName())
                .price(zone.getPrice())
                .build();
    }
}
