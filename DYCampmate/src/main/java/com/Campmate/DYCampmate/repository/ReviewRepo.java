package com.Campmate.DYCampmate.repository;

import com.Campmate.DYCampmate.entity.CampingZone;
import com.Campmate.DYCampmate.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<ReviewEntity, Long> {

    // 특정 캠핑존의 모든 리뷰를 ID 내림차순(최신순)으로 조회
    List<ReviewEntity> findByCampingZoneIdOrderByIdDesc(Long campingZoneId);

    List<ReviewEntity> findByCampingZone(CampingZone campingZone);

}