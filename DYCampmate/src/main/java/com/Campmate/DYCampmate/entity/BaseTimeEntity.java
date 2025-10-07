package com.Campmate.DYCampmate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // JPA 엔티티 클래스들이 이 클래스를 상속할 경우 필드들도 칼럼으로 인식
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 포함
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false, name="created_dt")
    private LocalDateTime createdDt;

    @LastModifiedDate
    @Column(name="updated_dt")
    private LocalDateTime updatedDt;
}