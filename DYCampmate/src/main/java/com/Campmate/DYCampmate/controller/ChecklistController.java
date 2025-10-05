package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.ChecklistCategoryDTO;
import com.Campmate.DYCampmate.dto.ChecklistItemDTO;
import com.Campmate.DYCampmate.entity.ChecklistCategoryEntity;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.service.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklist")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService checklistItemService;

    // 고객별 체크리스트 조회
    @PostMapping("/getChecklist/{customerId}")
    public ResponseEntity<List<ChecklistItemDTO>> getChecklist(@PathVariable Long customerId) {
        CustomerEntity customer = CustomerEntity.builder().id(customerId).build();
        return ResponseEntity.ok(checklistItemService.getChecklistByCustomer(customer));
    }
    // 카테고리 + 해당 아이템 전체 조회
    @GetMapping("/categories-with-items")
    public ResponseEntity<List<ChecklistCategoryDTO>> getCategoriesWithItems() {
        List<ChecklistCategoryDTO> response = checklistItemService.getAllCategoriesWithItems();
        return ResponseEntity.ok(response);
    }

    // 체크리스트 항목 추가
    @PostMapping("/getAddItem/{customerId}")
    public ResponseEntity<ChecklistItemDTO> addItem(@PathVariable Long customerId,
                                                    @RequestParam(required = false) Long categoryId,
                                                    @RequestParam String itemName) {
        CustomerEntity customer = CustomerEntity.builder().id(customerId).build();
        ChecklistCategoryEntity category = categoryId != null ? ChecklistCategoryEntity.builder().id(categoryId).build() : null;

        ChecklistItemDTO dto = checklistItemService.addChecklistItem(customer, category, itemName);
        return ResponseEntity.ok(dto);
    }

    // 체크 상태 업데이트
    @PutMapping("/{itemId}")
    public ResponseEntity<ChecklistItemDTO> updateChecked(@PathVariable Long itemId,
                                                          @RequestParam Boolean isChecked) {
        return ResponseEntity.ok(checklistItemService.updateChecked(itemId, isChecked));
    }

    // 체크리스트 항목 삭제
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        checklistItemService.deleteChecklistItem(itemId);
        return ResponseEntity.noContent().build();
    }
}