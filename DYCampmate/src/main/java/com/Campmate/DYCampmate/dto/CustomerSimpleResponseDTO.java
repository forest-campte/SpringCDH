package com.Campmate.DYCampmate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerSimpleResponseDTO {
    // 회원가입 응답
    private boolean success;
    private String message;
}
