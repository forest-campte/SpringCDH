package com.Campmate.DYCampmate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "checklist_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 고객(FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customers_id", referencedColumnName = "id", nullable = false)
    private CustomerEntity customer;

    // 카테고리(FK, 사용자가 직접 추가하면 null)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private ChecklistCategoryEntity category;

    @Column(name = "item_name", length = 255)
    private String itemName; // 아이템 이름

    @Builder.Default
    @Column(name = "is_checked", nullable = false)
    private Boolean isChecked = false; // 체크 여부
}
