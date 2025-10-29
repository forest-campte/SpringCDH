package com.Campmate.DYCampmate.dto;

// JPQL 쿼리 결과를 임시로 받기 위한 레코드
public record ZoneAdminRatingDTO(
        Long id,             // Zone ID
        String name,         // Zone Name
        String description,  // Zone Description
        String imageUrl,     // Zone ImageUrl
        Double rating,       // Zone Avg Rating
        Long adminId,        // Admin ID
        String adminName     // Admin Name
) {
    public ZoneHomeViewDTO toZoneHomeViewDTO() {
        return ZoneHomeViewDTO.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .imageUrl(this.imageUrl)
                .rating(this.rating)
                .build();
    }
}
