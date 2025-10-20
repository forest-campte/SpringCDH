package com.Campmate.DYCampmate.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerLoginResponseDTO {
    private Long id;
    private String userName;
    private String token;
}
