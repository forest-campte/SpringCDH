package com.Campmate.DYCampmate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter

public class CustomerFindIdResponseDTO {
    private boolean success;
    private String customerId;
    private String message;

    public static CustomerFindIdResponseDTO success(String id) {
        return new CustomerFindIdResponseDTO(true, id, "아이디 찾기 성공");
    }

    public static CustomerFindIdResponseDTO fail(String msg) {
        return new CustomerFindIdResponseDTO(false, null, msg);
    }



}