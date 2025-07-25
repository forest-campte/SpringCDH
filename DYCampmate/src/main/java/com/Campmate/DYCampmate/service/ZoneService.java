package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.dto.ZoneDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.repository.ReservationRepo;
import com.Campmate.DYCampmate.repository.ZoneRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneService {
    private final ZoneRepo zoneRepo;

    public ZoneService(ZoneRepo zoneRepo) {this.zoneRepo = zoneRepo;}

    public List<ZoneDTO> getZonesForAdmin(AdminEntity admin){
        return zoneRepo.findByAdmin(admin)
                .stream()
                .map(ZoneDTO::new)
                .collect(Collectors.toList());
    }
}
