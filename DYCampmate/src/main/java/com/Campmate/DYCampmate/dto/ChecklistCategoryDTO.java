package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.ChecklistCategoryEntity;
import lombok.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistCategoryDTO {
    private Long id;
    private String category;
    private String itemName;
    private List<ChecklistItemDTO> items;

    public ChecklistCategoryDTO(ChecklistCategoryEntity entity) {
        this.id = entity.getId();
        this.category = entity.getCategory();
        this.itemName = entity.getItemName();

        if (entity.getChecklistItems() != null) {
            this.items = entity.getChecklistItems().stream()
                    .map(ChecklistItemDTO::new)
                    .collect(Collectors.toList());
        }
    }
}
