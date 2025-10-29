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
        Double rating
) {

    //JPQL "new" 키워드가 이 생성자를 이용하여 DTO 객체를 만듦.

}

//        val id: Int,
//        val name: String,
//        val description: String,
//        val imageUrl: String,
//        val rating: Float,
//        //  이 캠핑장이 보유한 사이트(존) 목록
//        val sites: List<CampsiteSite> = emptyList() // 상세 정보 화면에서만 사용될 수 있음
