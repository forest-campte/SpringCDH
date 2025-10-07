package com.Campmate.DYCampmate.repository;

import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepo extends JpaRepository<ReservationEntity, Long> {

    //해당 admin의 모든 status
    List<ReservationEntity> findByCampingZone_Admin(AdminEntity admin);

    //해당 admin의 특정 1개 status
    List<ReservationEntity> findByCampingZone_AdminAndStatus(AdminEntity admin, ReservationEntity.ReservationStatus status);
    /**
     * 특정 관리자 ID에 속한 모든 예약을 조회합니다.
     * Fetch Join을 사용하여 N+1 문제를 해결합니다.
     * @param adminId 관리자 ID
     * @return 예약 엔티티 목록
     */
    @Query("SELECT r FROM ReservationEntity r JOIN FETCH r.campingZone cz WHERE cz.admin.id = :adminId")
    List<ReservationEntity> findReservationsByAdminIdWithDetails(@Param("adminId") Long adminId);

    List<ReservationEntity> findByCampingZone_AdminAndStatusIn(AdminEntity admin, List<ReservationEntity.ReservationStatus> statuses);
    /*여러 상태 지정 가능 R C E
    reservationRepo.findByCampingZone_AdminAndStatusIn(
    admin,
    List.of(ReservationEntity.ReservationStatus.R, ReservationEntity.ReservationStatus.C, ReservationEntity.ReservationStatus.E)
);
     */
}
