package com.Campmate.DYCampmate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "checklist_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"customer"}) // 순환 참조 방지를 위해 ToString 추가
public class ChecklistItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //customers -> id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customers_id", referencedColumnName = "id", nullable = false)
    private CustomerEntity customer;

    //checklist_category -> id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ChecklistCategoryEntity category;

    @Column(name = "item_name", length = 255)
    private String itemName;

    //체크 여부 (체크=1)
    @Column(name = "is_checked", nullable = false)
    @Builder.Default // Lombok 빌더 사용 시 기본값 설정
    private Boolean isChecked = false;

    //총개수
    @Column(name= "total", nullable = false)
    @Builder.Default
    private Integer total = 1;
}
