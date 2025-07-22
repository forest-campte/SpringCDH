package com.Campmate.DYCampmate.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Data Transfer Object
// Controller <-> Service 간 전달용 객체
public class CustomerRequestDTO {
    private String customerId;
    private String password;
    private String email;
    private String nickname;
    private String customersStyle;
    private String customersBackground;
    private String customersType;
    private LocalDateTime createDt;
    private String provider;

}
