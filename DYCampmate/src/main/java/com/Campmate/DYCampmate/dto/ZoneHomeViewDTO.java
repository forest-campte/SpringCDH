package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.CampingZone;
import com.Campmate.DYCampmate.entity.ReviewEntity;
import lombok.Builder;

@Builder
public record ZoneHomeViewDTO(
        Long id,
        String name,
        String description,
        String imageUrl,
        //평균 리뷰
        Double rating
) {

    //JPQL "new" 키워드가 이 생성자를 이용하여 DTO 객체를 만듦.

}
