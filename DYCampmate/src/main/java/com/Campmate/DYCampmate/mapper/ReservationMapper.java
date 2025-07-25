package com.Campmate.DYCampmate.mapper;

import com.Campmate.DYCampmate.dto.ReservationDTO;
import com.Campmate.DYCampmate.entity.ReservationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target="zoneName", source="campingZone.name")
    ReservationDTO toDto(ReservationEntity entity);

    List<ReservationDTO> toDtoList(List<ReservationEntity> entities);
}
