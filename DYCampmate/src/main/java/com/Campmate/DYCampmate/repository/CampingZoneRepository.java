package com.Campmate.DYCampmate.repository;

import com.Campmate.DYCampmate.dto.ZoneAdminRatingDTO;
import com.Campmate.DYCampmate.dto.ZoneHomeViewDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CampingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampingZoneRepository extends JpaRepository<CampingZone,Long> {

    List<CampingZone> findByAdmin(AdminEntity admin);
    List<CampingZone> findAllByAdmin_Id(Long adminId);
    List<CampingZone> findAllByAdmin(AdminEntity admin);
    // 캠핑존 수정/삭제 시 소유권 확인을 위해
    Optional<CampingZone> findByIdAndAdmin(Long id, AdminEntity admin);
    /**
     * 홈 화면에 보여줄 모든 캠핑존의 정보와 평균 평점을 조회하는 쿼리
     * JPQL Constructor Expression을 사용하여 즉시 DTO로 변환합니다.
     * N+1 문제를 해결하여 성능을 최적화합니다.
    */
    @Query("SELECT new com.Campmate.DYCampmate.dto.ZoneHomeViewDTO(cz.id, cz.name, cz.description, cz.imageUrl, CAST(COALESCE(AVG(r.rating), 0.0) AS double)) " +
            "FROM CampingZone cz LEFT JOIN cz.reviews r " +
            "GROUP BY cz.id " +
            "ORDER BY cz.id DESC")
    List<ZoneHomeViewDTO> findAllWithAverageRating_Original();

    /**
     * ✅ [이 쿼리 추가]
     * 특정 AdminEntity에 속한 모든 CampingZone의 정보와 평균 평점을
     * ZoneHomeViewDTO로 직접 조회합니다.
     */
    @Query("SELECT new com.Campmate.DYCampmate.dto.ZoneHomeViewDTO(" +
            "cz.id, cz.name, cz.description, cz.imageUrl, " +
            "CAST(COALESCE(AVG(r.rating), 0.0) AS double)) " +
            "FROM CampingZone cz " +
            "LEFT JOIN cz.reviews r ON r.campingZone = cz " +
            "WHERE cz.admin = :admin " + // Admin 객체로 필터링
            "GROUP BY cz.id")
    List<ZoneHomeViewDTO> findAllByAdminWithAverageRating(@Param("admin") AdminEntity admin);

    List<CampingZone> findByAdmin_Id(Long adminId);
//    @Query("SELECT new com.Campmate.DYCampmate.dto.ZoneAdminRatingDTO(" +
//            "    cz.id, cz.name, cz.description, cz.imageUrl, " +
//            "    CAST(COALESCE(AVG(r.rating), 0.0) AS double), " +
//            "    a.id, a.name) " +
//            "FROM CampingZone cz " +
//            "JOIN cz.admin a " +
//            "LEFT JOIN cz.reviews r ON r.campingZone = cz " +
//            "GROUP BY cz.id, a.id, a.name " + // GROUP BY에 admin 정보 추가
//            "ORDER BY a.id, cz.id DESC") // 관리자별로 정렬
//    List<ZoneAdminRatingDTO> findAllWithAdminAndAverageRating();


}
