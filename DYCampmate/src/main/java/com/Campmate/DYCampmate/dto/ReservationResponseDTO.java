package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.CampingZone;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {
    private Long reservationId;
    private String selectedSiteName; // 예약한 사이트 이름
    private String checkInDate;
    private String checkOutDate;
    private int adults;
    private int children;

    private CampingZone campsite; // 예약한 사이트 이름

}
