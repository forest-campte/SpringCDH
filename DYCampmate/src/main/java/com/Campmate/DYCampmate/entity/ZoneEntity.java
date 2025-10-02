package com.Campmate.DYCampmate.entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "camping_zone")
public class ZoneEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 관리자 (admins 테이블 참조)
    //외래키 패치지연 적용, 참고 컬럼 이름 명시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admins_id", referencedColumnName = "id")
    private AdminEntity admin;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String description;

    // 수용 인원 수
    @Column
    private Integer capacity;

    // 1박당 가격
    @Column(name = "price")
    private Integer pricePerNight;

    @Column(length = 255)
    private String type;

    @Column(name = "default_size", length = 255)
    private String defaultSize;

    // 1박당 가격
    @Column(length = 255)
    private String floor;

    // BIT(1) → Boolean
    @Column(name="parking")
    private Boolean parking;

    // BIT(1) → Boolean
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_dt")
    private LocalDateTime createdDt;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

}
