package com.Campmate.DYCampmate.dto;

public record CampingZoneUpdateRequestDto(
        String name,
        String description,
        int capacity,
        int price,
        String type,
        String defaultSize,
        String floor,
        Integer parking,
        Integer isActive
) {
}
