package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.ZoneEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZoneDTO {

    private Long id;
    private String name;
    private String description;
    private Integer capacity;
    private Integer pricePerNight;
    private String type;
    private String defaultSize;
    private String floor;
    private Boolean parking;
    private Boolean isActive;

    public ZoneDTO(ZoneEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.capacity = entity.getCapacity();
        this.pricePerNight = entity.getPricePerNight();
        this.type = entity.getType();
        this.defaultSize = entity.getDefaultSize();
        this.floor = entity.getFloor();
        this.parking = entity.getParking();
        this.isActive = entity.getIsActive();
    }


}
