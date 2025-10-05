package com.Campmate.DYCampmate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampingZoneRequest {
    private String name;
    private String description;
    private int capacity;
    private int price;
    private String type;
    private String default_size;
    private String floor;
    private boolean parking;
    private boolean active;
}