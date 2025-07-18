package com.Campmate.DYCampmate.entity;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// DB 테이블 구조와 매핑되는 클래스
@Entity
@Table(name = "customers")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customers_id", nullable = false, length = 100)
    private String customerId;

    @Column(name = "customers_password", nullable = false, length = 100)
    private String password;

    @Column(name = "customers_email", nullable = false, length = 100)
    private String email;

    @Column(name = "customers_nickname", length = 100)
    private String nickname;

    //캠핑 스타일
    @Column(name = "customers_style", length = 100)
    private String customersStyle;

    //캠핑 배경
    @Column(name = "customers_background", nullable = false, length = 100)
    private String customersBackground;

    //캠핑 동행자
    @Column(name = "customers_type", nullable = false, length = 100)
    private String customersType;

    @Column(name = "create_dt")
    private LocalDateTime createdDate;
}
