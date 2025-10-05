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
@Table(name = "camping_zones")
public class ZoneEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 관리자 (admins 테이블 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admins_id", referencedColumnName = "id", nullable = false)
    private AdminEntity admin;

    @Column(length = 255)
    private String name; // 캠핑존 이름

    @Column(length = 255)
    private String description; // 캠핑존 설명

    @Column
    private Integer capacity; // 수용 인원 수

    @Column(nullable = false)
    private Integer price; // 가격 (원본 테이블명 기준: price)

    @Column(length = 255)
    private String type; // 캠핑존 타입 (예: 오토캠핑, 글램핑 등)

    @Column(name = "default_size", length = 255)
    private String defaultSize; // 기본 크기

    @Column(length = 255)
    private String floor; // 바닥 종류 (예: 잔디, 데크 등)

    // BIT(1) → Boolean 자동 매핑
    @Column(name = "parking", nullable = false)
    private Boolean parking = false; // 주차 가능 여부

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false; // 예약 가능 여부

    @Column(name = "created_dt")
    private LocalDateTime createdDt;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    @PrePersist
    public void prePersist() {
        this.createdDt = LocalDateTime.now();
        this.updatedDt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDt = LocalDateTime.now();
    }
}
