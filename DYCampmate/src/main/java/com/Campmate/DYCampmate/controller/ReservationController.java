package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.ReservationDTO;
import com.Campmate.DYCampmate.dto.ReservationRequestDTO;
import com.Campmate.DYCampmate.dto.ReservationResponseDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.ReservationEntity;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.service.AdminService;
import com.Campmate.DYCampmate.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final AdminRepo adminRepository;

    // 로그인한 Admin 아이디를 가져와서 조회함.
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getMyReservations(@AuthenticationPrincipal User user) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 📝 HttpStatus import 확인
//        }
        // (수정) SecurityContextHolder에서 다시 꺼낼 필요 없이 주입받은 user 객체 사용
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

//        Long adminId = Long.parseLong(authentication.getName());
        // user.getUsername()이 Admin ID를 반환한다고 가정
        Long adminId = Long.parseLong(user.getUsername());

        // --- [핵심 수정 1] ---
        // adminId로 AdminEntity 조회
        AdminEntity currentAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with ID: " + adminId));
        // 서비스 메서드에 AdminEntity 전달
        List<ReservationDTO> reservations = reservationService.getReservationsForAdmin(currentAdmin);
        // -----------------------

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
        // adminId로 AdminEntity 조회
        AdminEntity admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with ID: " + adminId));
        // 서비스 메서드에 AdminEntity 전달
        List<ReservationDTO> reservations = reservationService.getReservationsForAdmin(admin);

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
//            @RequestParam("status") List<ReservationEntity.ReservationStatus> status) { // String 대신 Enum 타입으로 직접 받도록 변경
            // (수정) List -> 단일 Enum. 여러 개를 받으려 했으나 서비스가 단일 처리만 함.
            @RequestParam("status") ReservationEntity.ReservationStatus status) {

        // adminId로 AdminEntity 조회
        AdminEntity admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with ID: " + adminId));

        // 서비스 메서드 확인 및 호출
        if (status == null ) {
            return ResponseEntity.badRequest().body(null);
        }
        // --- [핵심 수정 3] ---
        // 서비스 메서드 이름과 파라미터 확인 (단일 상태 조회)
        List<ReservationDTO> reservations = reservationService.getReservationsByStatus(admin, status);
        // -----------------------


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

        // adminId로 AdminEntity 조회
        AdminEntity admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with ID: " + adminId));
        // 서비스 메서드에 AdminEntity 전달 (변경 없음)
        List<ReservationDTO> reservations =
                reservationService.getReservationsByStatuses(admin, statuses);

        return ResponseEntity.ok(reservations);
    }



    /**
     * 고객의 예약 목록 조회 API
     * GET("api/reservations/{customerId}")
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations(
            @PathVariable Long customerId
    ) {
        List<ReservationResponseDTO> reservations = reservationService.getMyReservations(customerId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * 나의 예약 목록을 (위도/경도 포함) 반환
     * ❗️(참고: ReservationRepository의 fetchMyReservations는 StateFlow를 사용하므로,
     * Spring에서는 별도의 Service 메서드를 만드는 것이 좋습니다.)
     */
    @GetMapping("/api/my-reservations/{customerId}")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservationsWithCoordinates(
            @PathVariable Long customerId
    ) {
        // 3. 좌표 변환 로직이 포함된 새 서비스 메서드 호출
        List<ReservationResponseDTO> reservationDtos =
                reservationService.getMyReservationsWithCoordinates(customerId);

        return ResponseEntity.ok(reservationDtos);
    }

    // 예약 취소
    @PutMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return "예약이 취소되었습니다.";
    }


    /**
     * 예약 생성 API
     * POST("api/reservations/make")
     */
    @PostMapping("make")
    public ResponseEntity<Void> makeReservation(
            @RequestBody ReservationRequestDTO request,
            Principal principal // Spring Security가 authToken에서 사용자 정보(email/id)를 추출
    ) {
        // principal.getName()은 보통 사용자의 ID(email)를 반환
        reservationService.makeReservation(request, principal.getName());

        return ResponseEntity.ok().build(); // 성공 시 200 OK 반환
    }




}
