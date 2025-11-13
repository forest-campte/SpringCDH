package com.Campmate.DYCampmate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyReviewResponseDTO {
    /**
    private Long id;

    @JsonProperty("reservation_id")
    private Long reservationId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("camping_zone_id")
    private Long campingZoneId;

//    @JsonProperty("campsiteName")
//    private String campsiteName;
//
//    @JsonProperty("campsite_name")
//    private String customerName;
//
//    @JsonProperty("author_name")
//    private String authorName;

    private int rating;
    private String coment;

    @JsonProperty("create_dt")
    private LocalDateTime createdDt;
    **/

    private Long id; // 리뷰 ID

    // ⬇️ --- 프론트엔드에서 요청한 'campsiteName' ---
    private String campsiteName;

    private int rating;

    private String coment;

    @JsonProperty("create_dt")
    private LocalDateTime createdDt; // 생성 날짜
}
