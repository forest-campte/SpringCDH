package com.Campmate.DYCampmate.repository;

import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepo extends JpaRepository<ReservationEntity, Long> {

    //해당 admin의 모든 status
    List<ReservationEntity> findByCampingZone_Admin(AdminEntity admin);

    //해당 admin의 특정 1개 status
    List<ReservationEntity> findByCampingZone_AdminAndStatus(AdminEntity admin, ReservationEntity.ReservationStatus status);
    
    List<ReservationEntity> findByCampingZone_AdminAndStatusIn(AdminEntity admin, List<ReservationEntity.ReservationStatus> statuses);
    /*여러 상태 지정 가능 R C E
    reservationRepo.findByCampingZone_AdminAndStatusIn(
    admin,
    List.of(ReservationEntity.ReservationStatus.R, ReservationEntity.ReservationStatus.C, ReservationEntity.ReservationStatus.E)
);
     */
}
