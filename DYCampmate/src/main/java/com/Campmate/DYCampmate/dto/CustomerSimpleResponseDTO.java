package com.Campmate.DYCampmate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerSimpleResponseDTO {
    private boolean success;
    private String message;
}
