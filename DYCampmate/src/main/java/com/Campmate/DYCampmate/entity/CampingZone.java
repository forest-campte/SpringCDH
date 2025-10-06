package com.Campmate.DYCampmate.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "camping_zones")
public class CampingZone extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admins_id", referencedColumnName = "id", nullable = false)
    private AdminEntity admin;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String description;

    private Integer capacity;

    @Column(nullable = false)
    private Integer price;

    @Column(length = 255)
    private String type;

    @Column(length = 255)
    private String defaultSize;

    @Column(length = 255)
    private String floor;

    @Column(nullable = false)
    private boolean parking;

    @Column(nullable = false)
    private boolean isActive;

    @Builder
    public CampingZone(AdminEntity admin, String name, String description, Integer capacity, Integer price, String type, String defaultSize, String floor, boolean parking, boolean isActive) {
        this.admin = admin;
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.price = price;
        this.type = type;
        this.defaultSize = defaultSize;
        this.floor = floor;
        this.parking = parking;
        this.isActive = isActive;
    }

    // 수정을 위한 메서드
    public void update(String name, String description, Integer capacity, Integer price, String type, String defaultSize, String floor, boolean parking, boolean isActive) {
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.price = price;
        this.type = type;
        this.defaultSize = defaultSize;
        this.floor = floor;
        this.parking = parking;
        this.isActive = isActive;
    }
}