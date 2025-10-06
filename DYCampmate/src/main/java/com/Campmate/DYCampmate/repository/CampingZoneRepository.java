package com.Campmate.DYCampmate.repository;

import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CampingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampingZoneRepository extends JpaRepository<CampingZone,Long> {

    // 특정 관리자 ID가 소유한 캠핑존 목록을 찾기 위한 메서드 (인증 로직에 따라 필요할 수 있음)
    List<CampingZone> findAllByAdmin_Id(Long adminsId);

    List<CampingZone> findByAdmin(AdminEntity admin);

}
