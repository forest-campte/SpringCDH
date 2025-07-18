package com.Campmate.DYCampmate.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoginRequestDTO {
    private String customerId;
    private String password;
}
