package com.Campmate.DYCampmate.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Data Transfer Object
// Controller <-> Service 간 전달용 객체
public class CustomerResponseDTO {
    private Long id;
    private String customerId;
    private String email;
    private String nickname;
    private String customersStyle;
    private String customersBackground;
    private String customersType;


}
