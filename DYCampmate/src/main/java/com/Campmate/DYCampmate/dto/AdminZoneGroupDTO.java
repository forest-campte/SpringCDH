package com.Campmate.DYCampmate.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AdminZoneGroupDTO(
        String name, // 1. admins 테이블의 name
        List<ZoneHomeViewDTO> sites // 2. 해당 admin에 속한 CampingZone 리스트
                                    // admin 캠핑장
                                    // ex ) name, address, campingStyle,campingType, imageUrl,

) {



}
