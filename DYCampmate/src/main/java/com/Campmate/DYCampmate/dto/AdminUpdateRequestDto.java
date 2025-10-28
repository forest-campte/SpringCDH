package com.Campmate.DYCampmate.dto;

public record AdminUpdateRequestDto(
        String email,
        String name,
        String description,
        String campingStyle,
        String campingBackground,
        String campingType,
        String address,
        String imageUrl
) {
}
