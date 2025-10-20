package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.AdminEntity;
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





    public AdminResponseDTO(AdminEntity admin) {
        this.id = admin.getId();
        this.email = admin.getEmail();
        this.name = admin.getName();
        this.campingBackground = admin.getCampingBackground();
        this.campingStyle = admin.getCampingStyle();
        this.campingType = admin.getCampingType();
    }
}
