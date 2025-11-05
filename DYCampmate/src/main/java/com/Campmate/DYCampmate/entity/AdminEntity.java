package com.Campmate.DYCampmate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.List;
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

    /**
     * ✅ [추가]
     * CampsiteDetailDTO에 관리자 전화번호를 제공하기 위한 필드
     * (DB에는 'phone_number' 컬럼으로 저장된다고 가정)
     */
    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    /**
     * Admin(1)이 여러 CampingZone(N)을 가짐
     * 'mappedBy = "admin"'는 CampingZone 엔티티에 'admin'이라는 필드가 있음을 의미합니다.
     */
    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY)
    private List<CampingZone> campingZones;


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