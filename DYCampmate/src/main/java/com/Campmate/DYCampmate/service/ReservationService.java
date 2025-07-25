package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.dto.ReservationDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.entity.ReservationEntity;
import com.Campmate.DYCampmate.mapper.ReservationMapper;
import com.Campmate.DYCampmate.repository.ReservationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepo reservationRepo;
    private final ReservationMapper reservationMapper;

    public List<ReservationDTO> getAllReservations(){
        return reservationMapper.toDtoList(reservationRepo.findAll());
    }


    //해당 관리자가 소유한 캠핑존 중 "예약됨(R)" 상태의 예약만 조회
    public List<ReservationDTO> getReservation(AdminEntity admin) {
        return reservationMapper.toDtoList(reservationRepo.findByCampingZone_Admin(admin));
    }


}
