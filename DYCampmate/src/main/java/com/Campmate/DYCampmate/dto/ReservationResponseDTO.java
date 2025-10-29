package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.AdminEntity;
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
    private String reservationId;
    private CampingZone Campsite; // 캠핑장 객체
    private String checkInDate;
    private String checkOutDate;
    private Integer adults;
    private Integer children;
    private String selectedSiteName; //  캠핑존 이름
}
