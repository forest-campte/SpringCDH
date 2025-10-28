package com.Campmate.DYCampmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDTO {
    private String description;
    private String icon;
    private Double temperature; // 현재 온도
    private Integer humidity;   // 습도
    private String dt_txt;      // 예보 시간 (예: "2025-10-30 18:00:00")
}
