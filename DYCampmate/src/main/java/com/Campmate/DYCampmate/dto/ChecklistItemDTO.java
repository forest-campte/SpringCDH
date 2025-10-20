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
    private String itemName;
    private Boolean isChecked;

    public ChecklistItemDTO(ChecklistItemEntity entity) {
        this.id = entity.getId();
        this.customerId = entity.getCustomer().getId();
        this.itemName = entity.getItemName();
        this.isChecked = entity.getIsChecked();
    }
}
