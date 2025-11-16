package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.AdminEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    private String email;
    private String password;
    private String name;

    private String description;
    private String campingStyle;
    private String campingBackground;
    private String campingType;

    private LocalDateTime createDt;

    private String address;
    private String imageUrl;

    public static AdminDTO fromEntity(AdminEntity entity) {
        return AdminDTO.builder()
                .email(entity.getEmail())
                .password(null)
                .name(entity.getName())
                .description(entity.getDescription())
                .campingStyle(entity.getCampingStyle())
                .campingBackground(entity.getCampingBackground())
                .campingType(entity.getCampingType())
                .createDt(entity.getCreateDt())
                .address(entity.getAddress())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}
