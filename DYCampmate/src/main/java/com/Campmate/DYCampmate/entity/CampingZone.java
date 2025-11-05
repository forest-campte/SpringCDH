package com.Campmate.DYCampmate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED)
@Table(name = "camping_zones")
public class CampingZone extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admins_id", nullable = false)
    private AdminEntity admin;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "type", length = 255)
    private String type;

    @Column(name = "default_size", length = 255)
    private String defaultSize;

    @Column(name = "floor", length = 255)
    private String floor;

    @Column(name = "parking", nullable = false)
    private boolean parking;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "campingZone")
    private List<ReviewEntity> reviews = new ArrayList<>();

    @Builder
    public CampingZone(Long id, AdminEntity admin, String name, String description, Integer capacity, Integer price, String type, String defaultSize, String floor, boolean parking, boolean isActive, String imageUrl) {
        this.id = id;
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
        this.imageUrl = imageUrl;
    }

    /**
     * ✅ [핵심] 서비스 레이어에서 호출할 엔티티 수정 메서드
     * (기존 서비스 코드의 updateCampingZone 로직과 일치시킴)
     */
    public void update(String name, String description, int capacity, int price, String type, String defaultSize, String floor, boolean parking, boolean isActive, String imageUrl) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        this.capacity = capacity;
        this.price = price;
        if (type != null) this.type = type;
        if (defaultSize != null) this.defaultSize = defaultSize;
        if (floor != null) this.floor = floor;
        this.parking = parking;
        this.isActive = isActive;

        // imageUrl이 null로 전달되면 기존 값을 유지하지 않고 null로 덮어쓰지 않도록 방어
        // (파일이 없는 경우 updateCampingZone에서 기존 URL을 전달해줘야 함)
        this.imageUrl = imageUrl;
    }
}