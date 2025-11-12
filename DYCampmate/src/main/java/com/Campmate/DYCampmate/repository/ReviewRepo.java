package com.Campmate.DYCampmate.repository;

import com.Campmate.DYCampmate.entity.CampingZone;
import com.Campmate.DYCampmate.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<ReviewEntity, Long> {

    // 특정 캠핑존의 모든 리뷰를 ID 내림차순(최신순)으로 조회
    List<ReviewEntity> findByCampingZoneIdOrderByIdDesc(Long campingZoneId);

    List<ReviewEntity> findByCampingZone(CampingZone campingZone);

    // 반환 타입을 List<ReviewEntity>로 변경
    List<ReviewEntity> findAllByCampingZoneId(Long campingZoneId);

    // JPQL의 FROM 절을 ReviewEntity로 변경
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM ReviewEntity r WHERE r.campingZone.id = :zoneId")
    Double findAverageRatingByCampingZoneId(@Param("zoneId") Long zoneId);

    /**
     * === [캠핑존 삭제를 위해 추가] ===
     * 특정 캠핑존(부모)에 속한 모든 리뷰(자식)를 삭제합니다.
     */
    void deleteAllByCampingZone(CampingZone campingZone);
}