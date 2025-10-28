package com.Campmate.DYCampmate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "admins", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class AdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//  @Setter
    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 255)
    private String address;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, name = "camping_style", length = 255)
    private String campingStyle;

    @Column(nullable = false, name = "camping_background", length = 255)
    private String campingBackground;

    @Column(nullable = false, name = "camping_type", length = 255)
    private String campingType;

    @Column(name = "create_dt")
    private LocalDateTime createDt;


    public void update(String email, String name, String description, String campingStyle, String campingBackground, String campingType, String address, String imageUrl) {
        if (email != null) this.email = email;
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (campingStyle != null) this.campingStyle = campingStyle;
        if (campingBackground != null) this.campingBackground = campingBackground;
        if (campingType != null) this.campingType = campingType;
        if (address != null) this.address = address;
        if (imageUrl != null) this.imageUrl = imageUrl;
    }



}