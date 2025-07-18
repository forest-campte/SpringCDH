package com.Campmate.DYCampmate.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class AdminResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String password;

    private String campingStyle;
    private String campingBackground;
    private String campingType;


    public AdminResponseDTO(Long id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;

    }
}
