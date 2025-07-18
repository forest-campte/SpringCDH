package com.Campmate.DYCampmate.repository;

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


}
