package com.Campmate.DYCampmate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reservations")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 캠핑존 외래키 (연결된 camping_zones 테이블과 매핑)
    //외래키 패치지연 적용, 참고 컬럼 이름 명시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camping_zone_id", referencedColumnName = "id")
    private ZoneEntity campingZone;

    //실제 DB 컬럼명과 다를경우 name 속성 필수
    @Column(name = "customer_name", length = 255)
    private String customerName;
    //실제 DB 컬럼명과 다를경우 name 속성 필수
    @Column(name = "customer_phone", length = 255)
    private String customerPhone;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('R','C','E') COMMENT '(예약됨:R,취소됨:C,완료:E)'")
    private ReservationStatus status;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    public enum ReservationStatus {
        R, // 예약됨
        C, // 취소됨
        E  // 완료
    }


}