//package com.Campmate.DYCampmate.mapper;
//
//import com.Campmate.DYCampmate.dto.ReservationDTO;
//import com.Campmate.DYCampmate.dto.ReservationRequestDTO;
//import com.Campmate.DYCampmate.dto.ReservationResponseDTO;
//import com.Campmate.DYCampmate.entity.AdminEntity;
//import com.Campmate.DYCampmate.entity.CampingZone;
//import com.Campmate.DYCampmate.entity.CustomerEntity;
//import com.Campmate.DYCampmate.entity.ReservationEntity;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.Collectors;
//
////@Mapper(componentModel = "spring")
//@Component
//public class ReservationMapper {
//
////    @Mapping(target="zoneName", source="campingZone.name")
////    ReservationDTO toDto(ReservationEntity entity);
////
////    List<ReservationDTO> toDtoList(List<ReservationEntity> entities);
//
//    // Request → Entity 변환
//    public ReservationEntity toEntity(
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
//                .adults(request.getAdults())
//                .children(request.getChildren())
////                .selectedSiteName(zone.getName())
//                .status(ReservationEntity.ReservationStatus.R)
//                .build();
//    }
//
//    // Entity → Response 변환
//    public ReservationResponseDTO toResponse(ReservationEntity entity) {
//        return ReservationResponseDTO.builder()
//                .reservationId(entity.getId())
////                .selectedSiteName(entity.getSelectedSiteName())
//                .checkInDate(entity.getCheckIn().toString())
//                .checkOutDate(entity.getCheckOut().toString())
//                .adults(entity.getAdults())
//                .children(entity.getChildren())
//                .campsite(CampingZone.builder()
//                        .id(entity.getCampingZone().getId())
//                        .name(entity.getCampingZone().getName())
//                        .description(entity.getCampingZone().getDescription())
//                        .build())
//                .build();
//    }
//
//    public List<ReservationResponseDTO> toResponseList(List<ReservationEntity> entities) {
//        return entities.stream()
//                .map(this::toResponse) // this::toResponse 사용
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Entity → 기존 ReservationDTO 변환 (기존 서비스 호환용)
//     */
//    public ReservationDTO toDto(ReservationEntity entity) {
//        return ReservationDTO.builder()
//                .id(entity.getId())
//                .customerName(entity.getCustomerName())
//                .customerPhone(entity.getCustomerPhone())
//                .adults(entity.getAdults())
//                .children(entity.getChildren())
//                .checkIn(entity.getCheckIn())
//                .checkOut(entity.getCheckOut())
//                .status(entity.getStatus().name())
//                .createDt(entity.getCreateDt())
//                .zoneName(entity.getCampingZone().getName())
//                .build();
//    }
//
//    /**
//     * Entity 리스트 → DTO 리스트 변환
//     */
//    public List<ReservationDTO> toDtoList(List<ReservationEntity> entities) {
//        return entities.stream()
//                .map(this::toDto)
//                .collect(Collectors.toList());
//    }
//
//
//}
