package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.JwtUtil;
import com.Campmate.DYCampmate.dto.ReservationDTO;
import com.Campmate.DYCampmate.dto.ReservationRequestDTO;
import com.Campmate.DYCampmate.dto.ReservationResponseDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.CampingZone;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.entity.ReservationEntity;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.repository.CampingZoneRepository;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import com.Campmate.DYCampmate.repository.ReservationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepo reservationRepo;
//    private final ReservationMapper reservationMapper;
    private final CampingZoneRepository campingZoneRepo;
    private final AdminRepo adminRepo;
    private final CustomerRepo customerRepo;
    private final JwtUtil jwtUtil;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final WeatherService weatherService;

    /**
     * 특정 관리자(Admin)의 모든 예약을 조회하는 메서드
     * @param admin 현재 로그인된 관리자 엔티티
     */
    public List<ReservationDTO> getReservationsForAdmin(AdminEntity admin) {
        List<ReservationEntity> reservations = reservationRepo.findByCampingZone_Admin(admin);

        return reservations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> getAllReservations(){

        return reservationRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    // 특정 상태의 예약 조회
    public List<ReservationDTO> getReservationsByStatus(AdminEntity admin, ReservationEntity.ReservationStatus status) {

        return reservationRepo.findByCampingZone_AdminAndStatus(admin, status).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 여러 상태의 예약 조회 (R, C, E 등)
    public List<ReservationDTO> getReservationsByStatuses(AdminEntity admin, List<ReservationEntity.ReservationStatus> statuses) {

        return reservationRepo.findByCampingZone_AdminAndStatusIn(admin, statuses).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }



    public List<ReservationDTO> getReservationsByAdminAndStatus(Long adminId, List<ReservationEntity.ReservationStatus> status) {

        return reservationRepo.findByAdminIdAndStatusInWithCampingZone(adminId, status).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        // 임시 반환 (컴파일 에러 방지)
//        throw new UnsupportedOperationException("findByAdminIdAndStatusInWithCampingZone 메서드가 ReservationRepo에 정의되지 않았습니다.");
    }

    // (Principal.getName()이 customerId라고 가정)
    public void makeReservation(ReservationRequestDTO request, String userLoginId) {

        // 1.customerId로 CustomerEntity 찾기
        CustomerEntity customer = customerRepo.findByCustomerId(userLoginId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + userLoginId));

        // 2. campingZoneId로 CampingZone 엔티티 찾기
        CampingZone zone = campingZoneRepo.findById(request.getCampingZoneId())
                .orElseThrow(() -> new IllegalArgumentException("Zone not found: " + request.getCampingZoneId()));

        // 3. adminsId로 AdminEntity 찾기 (ReservationEntity에 필요)
        AdminEntity admin = adminRepo.findById(request.getAdminsId())
                .orElseThrow(() -> new IllegalArgumentException("Admin not found: " + request.getAdminsId()));

        // (4. TODO: 날짜 겹치는지 확인하는 유효성 검사 로직)

        // 5. ReservationEntity 생성 및 저장
        ReservationEntity reservation = ReservationEntity.builder()
                .customer(customer) //
                .campingZone(zone)
                .admin(admin) //
                .checkIn(LocalDate.parse(request.getCheckIn(), formatter))
                .checkOut(LocalDate.parse(request.getCheckOut(), formatter))
                .adults(request.getAdults())
                .children(request.getChildren())
                // .status(ReservationStatus.R) // @PrePersist가 처리
                .build();

        reservationRepo.save(reservation);
    }


//    public void makeReservation(String token, ReservationRequestDTO request) {
//        // "Bearer " 제거
//        String pureToken = token.startsWith("Bearer ") ? token.substring(7) : token;
//
//        String customerIdStr = jwtUtil.getCustomerIdFromToken(pureToken);
//        Long customerId = Long.parseLong(customerIdStr);
//
//        CustomerEntity customer = customerRepo.findById(customerId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객입니다."));
//
//        AdminEntity admin = adminRepo.findById(request.getAdminsId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));
//
//        CampingZone zone = campingZoneRepo.findById(Long.parseLong(request.getCampingZoneId()))
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 캠핑존입니다."));
//
//
//        ReservationEntity entity = this.toEntity(request, customer, admin, zone);
//        reservationRepo.save(entity);
//    }

    public List<ReservationResponseDTO> getMyReservations(Long customerId) {
        List<ReservationEntity> reservations = reservationRepo.findByCustomer_Id(customerId);

        return reservations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getReservationsByAdmin(Long adminId) {
        AdminEntity admin = AdminEntity.builder().id(adminId).build();
        List<ReservationEntity> reservations = reservationRepo.findByAdmin(admin);

        return reservations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelReservation(Long id) {
        ReservationEntity reservation = reservationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
        reservation.setStatus(ReservationEntity.ReservationStatus.C);
        reservationRepo.save(reservation);
    }


    // --- Mapper 로직을 대체하는 Private 헬퍼 메서드 ---

    /**
     * Entity → ReservationDTO 변환 (기존 서비스 호환용)
     */
    private ReservationDTO toDto(ReservationEntity entity) {
        return ReservationDTO.builder()
                .id(entity.getId())
                .customerName(entity.getCustomerName())
                .customerPhone(entity.getCustomerPhone())
                .adults(entity.getAdults()!= null ? entity.getAdults() : 0)
                .children(entity.getChildren() != null? entity.getChildren() : 0)
                .checkIn(entity.getCheckIn())
                .checkOut(entity.getCheckOut())
                .status(entity.getStatus().name())
                .createDt(entity.getCreateDt())
                .zoneName(entity.getCampingZone().getName())
                .build();
    }

    /**
     * Entity → ReservationResponseDTO 변환
     */
    private ReservationResponseDTO toResponse(ReservationEntity entity) {
        return ReservationResponseDTO.builder()
                .reservationId(entity.getId())
                .Campsite(CampingZone.builder()
                    .id(entity.getCampingZone().getId())
                    .name(entity.getCampingZone().getName())
                    .description(entity.getCampingZone().getDescription())
                    .build())
                .checkInDate(entity.getCheckIn().toString())
                .checkOutDate(entity.getCheckOut().toString())
                .adults(entity.getAdults()!= null ? entity.getAdults() : 0)
                .children(entity.getChildren() != null? entity.getChildren() : 0)
                .selectedSiteName(entity.getCampingZone().getName())
                .build();
    }

    /**
     * 나의 예약 목록을 "위도/경도가 포함된" DTO 리스트로 반환합니다.
     */
    public List<ReservationResponseDTO> getMyReservationsWithCoordinates(Long customerId) {

        // 1. DB에서 원본 예약 목록을 가져옵니다.
        List<ReservationEntity> dbReservations = reservationRepo.findByCustomer_Id(customerId);

        // 2. Stream을 사용해 각 Reservation을 DTO로 변환합니다.
        return dbReservations.stream()
                .map(this::mapReservationToDtoWithCoords)
                .collect(Collectors.toList());
    }

    /**
     * Reservation 엔티티를 ReservationResponseDto로 변환하고,
     * Geocoding을 통해 위도/경도를 채워 넣는 헬퍼 함수
     */
    private ReservationResponseDTO mapReservationToDtoWithCoords(ReservationEntity reservation) {

        // 3. ⭐️ 핵심: WeatherService를 호출해 좌표를 가져옵니다.
        WeatherService.Coordinates coords = weatherService.getCoordinatesFromAddress(reservation.getAdmin().getAddress());

        // 4. DTO 객체를 생성하고 데이터를 매핑합니다.
        ReservationResponseDTO dto = new ReservationResponseDTO();

        // (기존 필드 매핑)
        dto.setReservationId(reservation.getId());
        dto.setSelectedSiteName(reservation.getCampingZone().getName());
        dto.setCheckInDate(String.valueOf(reservation.getCheckIn())); // (형식이 맞는지 확인)
        dto.setCheckOutDate(String.valueOf(reservation.getCheckOut()));
        // ... (기타 필드)

        // 5. (추가) 변환된 좌표를 DTO에 설정합니다.
        if (coords != null) {
            dto.setLatitude(coords.lat);
            dto.setLongitude(coords.lon);
        } else {
            // 주소 변환 실패 시 null로 남겨둠 (또는 기본값 설정)
            dto.setLatitude(null);
            dto.setLongitude(null);
        }

        return dto;
    }

    /**
     * ReservationRequestDTO → Entity 변환
     */
//    private ReservationEntity toEntity(
//            ReservationRequestDTO request,
//            CustomerEntity customer,
//            AdminEntity admin,
//            CampingZone zone
//    ) {
//        return ReservationEntity.builder()
//                .customer(customer)
//                .admin(admin)
//                .campingZone(zone)
//                .checkIn(LocalDate.parse(request.getCheckIn()))
//                .checkOut(LocalDate.parse(request.getCheckOut()))
//                .adults(request.getAdults() != null ? request.getAdults() : 0)
//                .children(request.getChildren())
//                .status(ReservationEntity.ReservationStatus.R)
//                .build();
//    }
//매퍼 제거
//    /**
//     * 특정 관리자(Admin)의 모든 예약을 조회하는 메서드
//     * @param admin 현재 로그인된 관리자 엔티티
//     */
//    public List<ReservationDTO> getReservationsForAdmin(AdminEntity admin) {
//        // Repository 메서드 호출 시에도 AdminEntity 객체를 전달합니다.
//        List<ReservationEntity> reservations = reservationRepo.findByCampingZone_Admin(admin);
//        return reservationMapper.toDtoList(reservations);
//    }
//
//    public List<ReservationDTO> getAllReservations(){
//        return reservationMapper.toDtoList(reservationRepo.findAll());
//    }
//
//
//    // 특정 상태의 예약 조회
//    public List<ReservationDTO> getReservationsByStatus(AdminEntity admin, ReservationEntity.ReservationStatus status) {
//        return reservationMapper
//                .toDtoList(reservationRepo.findByCampingZone_AdminAndStatus(admin, status));
//    }
//
//    // 여러 상태의 예약 조회 (R, C, E 등)
//    public List<ReservationDTO> getReservationsByStatuses(AdminEntity admin, List<ReservationEntity.ReservationStatus> statuses) {
//        return reservationMapper
//                .toDtoList(reservationRepo.findByCampingZone_AdminAndStatusIn(admin, statuses));
//    }
//
//
//
//    public List<ReservationDTO> getReservationsByAdminAndStatus(Long adminId, List<ReservationEntity.ReservationStatus> status) {
//        // Repository 메서드가 Long을 받는지 확인 필요
//        // 현재 ReservationRepo에는 findByAdminIdAndStatusInWithCampingZone 메서드가 없으므로 컴파일 에러 발생 가능성 있음
//        // return reservationMapper
//        //        .toDtoList(reservationRepo.findByAdminIdAndStatusInWithCampingZone(adminId, status));
//        // 임시 반환 (컴파일 에러 방지)
//        throw new UnsupportedOperationException("findByAdminIdAndStatusInWithCampingZone 메서드가 ReservationRepo에 정의되지 않았습니다.");
//    }
//
//
//    public void makeReservation(String token, ReservationRequestDTO request) {
//        // "Bearer " 제거
//        String pureToken = token.startsWith("Bearer ") ? token.substring(7) : token;
//
//        String customerIdStr = jwtUtil.getCustomerIdFromToken(pureToken);
//        Long customerId = Long.parseLong(customerIdStr);
//
//        CustomerEntity customer = customerRepo.findById(customerId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객입니다."));
//
//        AdminEntity admin = adminRepo.findById(request.getAdminsId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));
//
//        CampingZone zone = campingZoneRepo.findById(Long.parseLong(request.getCampingZoneId()))
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 캠핑존입니다."));
//
//        // Mapper로 Entity 생성
//        ReservationEntity entity = reservationMapper.toEntity(request, customer, admin, zone);
//        reservationRepo.save(entity);
//    }
//
//    public List<ReservationResponseDTO> getMyReservations(Long customerId) {
//        List<ReservationEntity> reservations = reservationRepo.findByCustomer_Id(customerId);
//        return reservations.stream()
//                .map(reservationMapper::toResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<ReservationResponseDTO> getReservationsByAdmin(Long adminId) {
//        AdminEntity admin = AdminEntity.builder().id(adminId).build();
//        List<ReservationEntity> reservations = reservationRepo.findByAdmin(admin);
//        return reservationMapper.toResponseList(reservations);
//    }
//
//    @Transactional
//    public void cancelReservation(Long id) {
//        ReservationEntity reservation = reservationRepo.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
//        reservation.setStatus(ReservationEntity.ReservationStatus.C);
//        reservationRepo.save(reservation);
//    }




}
