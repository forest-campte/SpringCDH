package com.Campmate.DYCampmate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDTO {
    @JsonProperty("reservation_id")
    private Long reservationId;

    @JsonProperty("customers_Id")
    private Long customerId;

    @JsonProperty("camping_zone_id")
    private Long campingZoneId;

    private int rating;
    private String coment;
}
