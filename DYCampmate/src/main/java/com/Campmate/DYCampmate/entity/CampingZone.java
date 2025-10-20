package com.Campmate.DYCampmate.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "camping_zones")
public class CampingZone extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admins_id", nullable = false)
    private AdminEntity admin;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "type")
    private String type;

    @Column(name = "default_size")
    private String defaultSize;

    @Column(name = "floor")
    private String floor;

    @Column(name = "parking", nullable = false)
    private boolean parking;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "campingZone")
    private List<ReviewEntity> reviews = new ArrayList<>();

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