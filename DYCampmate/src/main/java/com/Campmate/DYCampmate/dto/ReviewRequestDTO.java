package com.Campmate.DYCampmate.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDTO {
    private Long reservationsId;
    private Long customersId;
    private Long campingZoneId;
    private int rating;
    private String coment;
}
