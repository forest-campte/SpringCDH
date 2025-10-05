package com.Campmate.DYCampmate.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "checklist_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", length = 100)
    private String category;  // 카테고리 명

    @Column(name = "item_name", length = 100)
    private String itemName;  // 아이템 명

    // checklist_item 과 1:N 관계
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItemEntity> checklistItems;
}
