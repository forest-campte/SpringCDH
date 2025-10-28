package com.Campmate.DYCampmate.dto;

import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDTO {
    private Long adminsId;
    private String campingZoneId;
    private String checkIn;
    private String checkOut;
    private int adults;
    private int children;

}