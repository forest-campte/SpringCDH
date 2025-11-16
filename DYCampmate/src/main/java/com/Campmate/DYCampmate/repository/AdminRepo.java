package com.Campmate.DYCampmate.repository;

import com.Campmate.DYCampmate.dto.ZoneHomeViewDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepo extends JpaRepository<AdminEntity, Long> {

    Optional<AdminEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    // 회원가입 -> 맞춤형 캠핑장 리스트 노출 시,
    // 회원가입 step에서 선택한 style, background, type 과 동일, 유사한 캠핑장 리스트 검색
    @Query("SELECT a FROM AdminEntity a " +
            "WHERE a.campingStyle LIKE %:style% " +
            "OR a.campingBackground LIKE %:background% " +
            "OR a.campingType LIKE %:type%")
    List<AdminEntity> findMatchingAdmins(@Param("style") String style,
                                         @Param("background") String background,
                                         @Param("type") String type);


    //1030km 어드민 웹 관련 수정
    /**
     * ✅ [이 쿼리 수정]
     * 'AdminEntity'의 'campingZones' 필드와 정확히 일치하도록 수정합니다.
     * (LEFT JOIN a.campingZones cz)
     */
    @Query("SELECT new com.Campmate.DYCampmate.dto.ZoneHomeViewDTO(" +
            "a.id, a.name, a.description, a.imageUrl, " +
            "CAST(COALESCE(AVG(r.rating), 0.0) AS double)) " +
            "FROM AdminEntity a " +
            // --- 이 부분을 'a.campingZones' (소문자 c, 복수형 s)로 수정 ---
            "LEFT JOIN a.campingZones cz " +
            // ---------------------------------------------------------
            "LEFT JOIN cz.reviews r " +
            "GROUP BY a.id, a.name, a.description, a.imageUrl " +
            "ORDER BY a.name")
    List<ZoneHomeViewDTO> findAllAsCampsiteList();

    // 25.11.14 KM 추가: 캠핑장 검색 쿼리 (Service에서 호출됨)
    @Query("SELECT a FROM AdminEntity a " +
            // 키워드가 null이 아니면 name 또는 address에 키워드가 포함되는지 확인
            "WHERE (:keyword IS NULL OR :keyword = '' OR a.name LIKE %:keyword% OR a.address LIKE %:keyword%) " +
            // 지역이 null이 아니면 address가 해당 지역으로 시작하는지 확인
            "AND (:region IS NULL OR :region = '' OR a.address LIKE CONCAT(:region, '%'))")
    List<AdminEntity> searchAdminsByKeywordAndRegion(
            @Param("keyword") String keyword,
            @Param("region") String region
    );
}
