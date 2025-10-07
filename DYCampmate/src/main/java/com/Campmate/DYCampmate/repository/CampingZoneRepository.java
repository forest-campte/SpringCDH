package com.Campmate.DYCampmate.repository;

import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CampingZone;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
