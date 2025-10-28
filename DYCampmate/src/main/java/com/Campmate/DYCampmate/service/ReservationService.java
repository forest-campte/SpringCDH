package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.JwtUtil;
import com.Campmate.DYCampmate.dto.ReservationDTO;
import com.Campmate.DYCampmate.dto.ReservationRequestDTO;
import com.Campmate.DYCampmate.dto.ReservationResponseDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CampingZone;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.entity.ReservationEntity;
import com.Campmate.DYCampmate.mapper.ReservationMapper;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.repository.CampingZoneRepository;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import com.Campmate.DYCampmate.repository.ReservationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepo reservationRepo;
    private final ReservationMapper reservationMapper;
    private final CampingZoneRepository campingZoneRepo;
    private final AdminRepo adminRepo;
    private final CustomerRepo customerRepo;
    private final JwtUtil jwtUtil;

    /**
     * 특정 관리자(Admin)의 모든 예약을 조회하는 메서드
     * @param admin 현재 로그인된 관리자 엔티티
     */
    public List<ReservationDTO> getReservationsForAdmin(AdminEntity admin) {
        // Repository 메서드 호출 시에도 AdminEntity 객체를 전달합니다.
        List<ReservationEntity> reservations = reservationRepo.findByCampingZone_Admin(admin);
        return reservationMapper.toDtoList(reservations);
    }

    public List<ReservationDTO> getAllReservations(){
        return reservationMapper.toDtoList(reservationRepo.findAll());
    }


    // 특정 상태의 예약 조회
    public List<ReservationDTO> getReservationsByStatus(AdminEntity admin, ReservationEntity.ReservationStatus status) {
        return reservationMapper
                .toDtoList(reservationRepo.findByCampingZone_AdminAndStatus(admin, status));
    }

    // 여러 상태의 예약 조회 (R, C, E 등)
    public List<ReservationDTO> getReservationsByStatuses(AdminEntity admin, List<ReservationEntity.ReservationStatus> statuses) {
        return reservationMapper
                .toDtoList(reservationRepo.findByCampingZone_AdminAndStatusIn(admin, statuses));
    }



    public List<ReservationDTO> getReservationsByAdminAndStatus(Long adminId, List<ReservationEntity.ReservationStatus> status) {
        // Repository 메서드가 Long을 받는지 확인 필요
        // 현재 ReservationRepo에는 findByAdminIdAndStatusInWithCampingZone 메서드가 없으므로 컴파일 에러 발생 가능성 있음
        // return reservationMapper
        //        .toDtoList(reservationRepo.findByAdminIdAndStatusInWithCampingZone(adminId, status));
        // 임시 반환 (컴파일 에러 방지)
        throw new UnsupportedOperationException("findByAdminIdAndStatusInWithCampingZone 메서드가 ReservationRepo에 정의되지 않았습니다.");
    }


    public void makeReservation(String token, ReservationRequestDTO request) {
        // "Bearer " 제거
        String pureToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        String customerIdStr = jwtUtil.getCustomerIdFromToken(pureToken);
        Long customerId = Long.parseLong(customerIdStr);

        CustomerEntity customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객입니다."));

        AdminEntity admin = adminRepo.findById(request.getAdminsId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

        CampingZone zone = campingZoneRepo.findById(Long.parseLong(request.getCampingZoneId()))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 캠핑존입니다."));

        // Mapper로 Entity 생성
        ReservationEntity entity = reservationMapper.toEntity(request, customer, admin, zone);
        reservationRepo.save(entity);
    }

    public List<ReservationResponseDTO> getMyReservations(Long customerId) {
        List<ReservationEntity> reservations = reservationRepo.findByCustomer_Id(customerId);
        return reservations.stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getReservationsByAdmin(Long adminId) {
        AdminEntity admin = AdminEntity.builder().id(adminId).build();
        List<ReservationEntity> reservations = reservationRepo.findByAdmin(admin);
        return reservationMapper.toResponseList(reservations);
    }

    @Transactional
    public void cancelReservation(Long id) {
        ReservationEntity reservation = reservationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
        reservation.setStatus(ReservationEntity.ReservationStatus.C);
        reservationRepo.save(reservation);
    }



}
