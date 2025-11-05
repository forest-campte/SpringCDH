package com.Campmate.DYCampmate.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FormData의 텍스트 필드를 바인딩하기 위한 DTO
 * @ModelAttribute로 사용되므로 @Data (또는 @Setter + @NoArgsConstructor)가 필요합니다.
 */
@Data
@NoArgsConstructor
public class CampingZoneFormDto {

    private String name;
    private String description;
    private int capacity;
    private int price;
    private String type;
    private String defaultSize;
    private String floor;

    // React에서 1(true) 또는 0(false)으로 전송하므로 Integer로 받습니다.
    private Integer parking;
    private Integer isActive;

    // imageFile은 @RequestParam으로 따로 받으므로 이 DTO에 포함되지 않습니다.
}