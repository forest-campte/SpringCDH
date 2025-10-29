package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.CampingZone;
import lombok.Builder;

import java.util.List;

// 메인 상세 정보 응답 DTO
@Builder
public record CampsiteDetailDTO(
        Long id,
        String name,
        String description,
        String imageUrl,
        Double rating, // 리뷰 평균 평점
        List<CampsiteSiteDTO> sites // 같은 관리자의 다른 모든 사이트 목록
) {
    // 엔티티와 계산된 평점, 사이트 리스트를 조합해 DTO 생성
    public static CampsiteDetailDTO fromEntity(CampingZone zone, Double rating, List<CampsiteSiteDTO> sites) {
        return CampsiteDetailDTO.builder()
                .id(zone.getId())
                .name(zone.getName())
                .description(zone.getDescription())
                .imageUrl(zone.getImageUrl())
                .rating(rating)
                .sites(sites)
                .build();
    }
}
