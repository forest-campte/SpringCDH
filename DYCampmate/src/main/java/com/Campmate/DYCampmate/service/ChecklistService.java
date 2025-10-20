package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.dto.ChecklistCategoryDTO;
import com.Campmate.DYCampmate.dto.ChecklistItemDTO;
import com.Campmate.DYCampmate.entity.ChecklistCategoryEntity;
import com.Campmate.DYCampmate.entity.ChecklistItemEntity;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.repository.ChecklistCategoryRepo;
import com.Campmate.DYCampmate.repository.ChecklistItemRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistItemRepo checklistItemRepository;
    private final ChecklistCategoryRepo checklistCategoryRepository;

    // 고객별 체크리스트 조회
    public List<ChecklistItemDTO> getChecklistByCustomer(CustomerEntity customer) {
        return checklistItemRepository.findByCustomer(customer)
                .stream()
                .map(ChecklistItemDTO::new)
                .collect(Collectors.toList());
    }
    //아이템의 카테고리 조회
    public List<ChecklistCategoryDTO> getAllCategoriesWithItems() {
        List<ChecklistCategoryEntity> categories = checklistCategoryRepository.findAll();
        return categories.stream()
                .map(ChecklistCategoryDTO::new)
                .collect(Collectors.toList());
    }


    // 체크리스트 항목 추가
    public ChecklistItemDTO addChecklistItem(CustomerEntity customer,
                                             String itemName) {
        ChecklistItemEntity entity = ChecklistItemEntity.builder()
                .customer(customer)
                .itemName(itemName)
                .isChecked(false)
                .build();

        return new ChecklistItemDTO(checklistItemRepository.save(entity));
    }

    // 체크 상태 업데이트
    public ChecklistItemDTO updateChecked(Long itemId, Boolean isChecked) {
        ChecklistItemEntity entity = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 체크리스트 항목 ID: " + itemId));

        entity.setIsChecked(isChecked);
        return new ChecklistItemDTO(checklistItemRepository.save(entity));
    }

    // 체크리스트 항목 삭제
    public void deleteChecklistItem(Long itemId) {
        checklistItemRepository.deleteById(itemId);
    }
}
