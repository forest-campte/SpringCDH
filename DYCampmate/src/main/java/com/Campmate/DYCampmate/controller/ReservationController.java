package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.ReservationDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.ReservationEntity;
import com.Campmate.DYCampmate.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 로그인한 Admin 아이디를 가져와서 조회함.
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getMyReservations(@AuthenticationPrincipal User user) {
        // @AuthenticationPrincipal을 통해 현재 로그인한 사용자의 ID(username 필드에 저장됨)를 가져옴
        // ID가 Long 타입이므로 변환
        Long adminId = Long.parseLong(user.getUsername());

        // 해당 관리자의 예약 목록을 조회
        List<ReservationDTO> reservations = reservationService.getReservationsForAdmin(adminId);

        return ResponseEntity.ok(reservations);
    }
    /**
     * 전체 예약 목록 조회
     * (관리자/고객 구분 없이 모든 예약을 조회)
     */
    @GetMapping("/all")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    /**
     * 특정 관리자(Admin)의 모든 예약 조회
     * @param adminId 관리자 ID
     */
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByAdmin(@PathVariable Long adminId) {
        AdminEntity admin = AdminEntity.builder().id(adminId).build();
        List<ReservationDTO> reservations = reservationService.getReservation(admin);
        return ResponseEntity.ok(reservations);
    }



    /**
     * 예: GET /api/reservations/admin/1/status?status=R
     *
     * @param adminId 관리자 ID (URL 경로에서 추출)
     * @param status 예약 상태 코드 (요청 파라미터에서 추출: 'R', 'C', 'E')
     * @return 상태에 따라 필터링된 예약 목록
     */
    @GetMapping("/admin/{adminId}/status")
    public ResponseEntity<List<ReservationDTO>> getReservationsByAdminAndStatus(
            @PathVariable Long adminId,
            @RequestParam("status") List<ReservationEntity.ReservationStatus> status) { // String 대신 Enum 타입으로 직접 받도록 변경

        // 서비스 레이어에 adminId와 status를 직접 전달하여 로직 처리
        List<ReservationDTO> reservations = reservationService.getReservationsByAdminAndStatus(adminId, status);

        // 조회된 예약 목록을 반환
        return ResponseEntity.ok(reservations);
    }

    /**
     * 특정 관리자(Admin)의 여러 상태(Status 목록)의 예약 조회
     * 예: /api/reservations/admin/1/statuses?statuses=R,C,E
     */
    @GetMapping("/admin/{adminId}/statuses")
    public ResponseEntity<List<ReservationDTO>> getReservationsByAdminAndStatuses(
            @PathVariable Long adminId,
            @RequestParam List<ReservationEntity.ReservationStatus> statuses) {

        AdminEntity admin = AdminEntity.builder().id(adminId).build();
        List<ReservationDTO> reservations =
                reservationService.getReservationsByStatuses(admin, statuses);

        return ResponseEntity.ok(reservations);
    }



}
