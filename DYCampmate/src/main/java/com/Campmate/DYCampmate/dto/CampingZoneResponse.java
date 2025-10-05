package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.ZoneEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CampingZoneResponse {
    private Long id;
    private String name;
    private String description;
    private int capacity;
    private int price;
    private String type;
    private String default_size;
    private String floor;
    private boolean parking;
    private boolean active;

    public static CampingZoneResponse fromEntity(ZoneEntity zone) {
        return CampingZoneResponse.builder()
                .id(zone.getId())
                .name(zone.getName())
                .description(zone.getDescription())
                .capacity(zone.getCapacity())
                .price(zone.getPrice())
                .type(zone.getType())
                .default_size(zone.getDefaultSize())
                .floor(zone.getFloor())
                .parking(zone.isParking())
                .active(zone.isActive())
                .build();
    }
}
