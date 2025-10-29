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
    private Long reservationId;
    private String adminName; // 캠핑장 이름
    private String ZoneName; //  캠핑존 이름
    private String checkInDate;
    private String checkOutDate;
    private Integer adults;
    private Integer children;

}
