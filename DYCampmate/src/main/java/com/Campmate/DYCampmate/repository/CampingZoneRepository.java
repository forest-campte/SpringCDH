package com.Campmate.DYCampmate.repository;

import com.Campmate.DYCampmate.dto.ZoneHomeViewDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CampingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampingZoneRepository extends JpaRepository<CampingZone,Long> {

    List<CampingZone> findByAdmin(AdminEntity admin);

    /**
     * 특정 관리자 ID에 속한 모든 캠핑존을 조회
     * @param adminId 관리자(Admin)의 ID
     * @return 해당 관리자의 캠핑존 목록
     */
    List<CampingZone> findAllByAdmin_Id(Long adminId);

    /**
     * 홈 화면에 보여줄 모든 캠핑존의 정보와 평균 평점을 조회하는 쿼리
     * JPQL Constructor Expression을 사용하여 즉시 DTO로 변환합니다.
     * N+1 문제를 해결하여 성능을 최적화합니다.
     */
    @Query("SELECT new com.Campmate.DYCampmate.dto.ZoneHomeViewDTO(cz.id, cz.name, cz.description, cz.imageUrl, CAST(COALESCE(AVG(r.rating), 0.0) AS double)) " +
            "FROM CampingZone cz LEFT JOIN cz.reviews r " +
            "GROUP BY cz.id")
    List<ZoneHomeViewDTO> findAllWithAverageRating();
}
