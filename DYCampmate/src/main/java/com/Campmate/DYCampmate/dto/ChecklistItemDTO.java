package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.ChecklistItemEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistItemDTO {
    private Long id;
    private Long customerId;
    private Long categoryId;
    private String itemName;
    private Boolean isChecked;

    public ChecklistItemDTO(ChecklistItemEntity entity) {
        this.id = entity.getId();
        this.customerId = entity.getCustomer().getId();
        this.categoryId = entity.getCategory() != null ? entity.getCategory().getId() : null;
        this.itemName = entity.getItemName();
        this.isChecked = entity.getIsChecked();
    }
}
