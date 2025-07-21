package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.AdminEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminDTO {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String description;
    private String campingStyle;
    private String campingBackground;
    private String campingType;
    private LocalDateTime createDt;

    public static AdminDTO fromEntity(AdminEntity admin) {
        return AdminDTO.builder()
                .id(admin.getId())
                .name(admin.getName())
                .description(admin.getDescription())
                .campingStyle(admin.getCampingStyle())
                .campingBackground(admin.getCampingBackground())
                .campingType(admin.getCampingType())
                .build();
    }

}
