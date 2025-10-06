package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.ReservationEntity;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private Long id;
    private String customerName;
    private String customerPhone;
    private String checkIn;
    private String checkOut;
    private String status;
    private String createdDt;

    private String zoneName;


}
