package com.Campmate.DYCampmate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예약 (Foreign Key)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservations_id", nullable = false)
    private ReservationEntity reservation;

    // 고객 (Foreign Key)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customers_id", nullable = false)
    private CustomerEntity customer;

    // 캠핑존 (Foreign Key)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camping_zone_id", nullable = false)
    private CampingZone campingZone;

    // 별점 (1~5)
    @Column(nullable = false)
    private int rating;

    // 리뷰 내용
    @Column(columnDefinition = "TEXT")
    private String coment;

    // 생성일
    @Column(name = "created_dt")
    private LocalDateTime createdDt;

    @PrePersist
    public void prePersist() {
        this.createdDt = LocalDateTime.now();
    }
}