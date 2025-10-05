package com.Campmate.DYCampmate.repository;
import com.Campmate.DYCampmate.entity.ChecklistItemEntity;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChecklistItemRepo extends JpaRepository<ChecklistItemEntity, Long>{
    // 특정 고객의 체크리스트 조회
    public List<ChecklistItemEntity> findByCustomer(CustomerEntity customer);

}
