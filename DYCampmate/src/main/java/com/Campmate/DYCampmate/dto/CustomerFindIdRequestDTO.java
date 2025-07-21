package com.Campmate.DYCampmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerFindIdRequestDTO {
        private String email;
        private String nickname;

}
