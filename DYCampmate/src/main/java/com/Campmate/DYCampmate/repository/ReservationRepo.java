package com.Campmate.DYCampmate.repository;

import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CampingZone;
import com.Campmate.DYCampmate.entity.CustomerEntity;
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

    /*
     * ------------------------------------------------------------------------------------
     * [개선된 쿼리 메소드]
     * 아래 메소드들은 JPQL과 JOIN FETCH를 사용하여 성능 문제를 해결하고,
     * Service 레이어에서 사용하기 편리하도록 Long 타입의 ID를 직접 파라미터로 받습니다.
     * ------------------------------------------------------------------------------------
     */

    /**
     * 🚀 [핵심 개선] 특정 관리자의 특정 상태(status) 예약을 조회합니다. (N+1 문제 해결)
     * 이 메소드가 이전에 요청하신 Controller의 기능을 가장 효율적으로 지원합니다.
     *
     * @param adminId 관리자 ID
     * @param status 예약 상태
     * @return 조회된 예약 목록
     */
    @Query("SELECT r FROM ReservationEntity r JOIN FETCH r.campingZone cz WHERE cz.admin.id = :adminId AND r.status = :status")
    List<ReservationEntity> findByAdminIdAndStatusWithCampingZone(@Param("adminId") Long adminId, @Param("status") ReservationEntity.ReservationStatus status);

    /**
     * 특정 관리자의 모든 예약을 조회합니다. (N+1 문제 해결)
     * (기존 findReservationsByAdminIdWithDetails 메소드와 동일 기능)
     *
     * @param adminId 관리자 ID
     * @return 조회된 예약 목록
     */
    @Query("SELECT r FROM ReservationEntity r JOIN FETCH r.campingZone cz WHERE cz.admin.id = :adminId")
    List<ReservationEntity> findAllByAdminIdWithCampingZone(@Param("adminId") Long adminId);

    /**
     * 특정 관리자의 여러 상태(statuses)에 해당하는 예약을 조회합니다. (N+1 문제 해결)
     *
     * @param adminId 관리자 ID
     * @param statuses 예약 상태 목록
     * @return 조회된 예약 목록
     */
    @Query("SELECT r FROM ReservationEntity r JOIN FETCH r.campingZone cz WHERE cz.admin.id = :adminId AND r.status IN :statuses")
    List<ReservationEntity> findByAdminIdAndStatusInWithCampingZone(@Param("adminId") Long adminId, @Param("statuses") List<ReservationEntity.ReservationStatus> statuses);

    List<ReservationEntity> findByCustomer(CustomerEntity customer);
    List<ReservationEntity> findByAdmin(AdminEntity admin);
    List<ReservationEntity> findByCampingZone(CampingZone zone);
    List<ReservationEntity> findByCustomer_Id(Long customerId);
}
