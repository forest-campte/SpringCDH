package com.Campmate.DYCampmate.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoginRequestDTO {

    private String customerId;
    private String customerPassword;
}
