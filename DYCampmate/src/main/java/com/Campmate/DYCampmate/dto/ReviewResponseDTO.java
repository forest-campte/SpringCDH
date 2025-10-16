package com.Campmate.DYCampmate.dto;

import com.Campmate.DYCampmate.entity.ReviewEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {
    private Long id;
    private String customerName;
    private String campingZoneName;
    private int rating;
    private String coment;
    private LocalDateTime createdDt;
}
