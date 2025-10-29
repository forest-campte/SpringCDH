package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.ReservationEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private Long id;
    private String customerName;
    private String customerPhone;
    private Integer adults;
    private Integer children;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String status;
    private LocalDateTime createDt;

    private String zoneName;

    // 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static ReservationDTO from(ReservationEntity entity) {
        return ReservationDTO.builder()
                .id(entity.getId())
                .customerName(entity.getCustomerName())
                .customerPhone(entity.getCustomerPhone())
                .adults(entity.getAdults())
                .children(entity.getChildren())
                .checkIn(entity.getCheckIn())
                .checkOut(entity.getCheckOut())
                .status(String.valueOf(entity.getStatus()))
                .createDt(entity.getCreateDt())
                .build();
    }
}
