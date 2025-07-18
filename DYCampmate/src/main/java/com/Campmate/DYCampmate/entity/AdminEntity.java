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
    private Long id;  // int(15) → Integer

    @Setter
    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 255)
    private String name;

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






}