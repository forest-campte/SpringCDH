package com.Campmate.DYCampmate.dto;

import lombok.*;

import java.time.LocalDate;

//예약 요청 시 프론트에서 보낼 JSON DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDTO {
    private Long adminsId;
    private Long campingZoneId;
    private String checkIn;
    private String checkOut;
    private Integer adults;
    private Integer children;

}