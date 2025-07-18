package com.Campmate.DYCampmate.service;

import com.Campmate.DYCampmate.JwtUtil;
import com.Campmate.DYCampmate.dto.CustomerLoginRequestDTO;
import com.Campmate.DYCampmate.dto.CustomerLoginResponseDTO;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.dto.CustomerRequestDTO;
import com.Campmate.DYCampmate.dto.CustomerResponseDTO;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
//실제 비즈니스 로직 처리
//트랜잭션, 예외 처리 포함
//Entity <-> DTO 변환도 여기서 주로 처리
public class CustomerService {
    private final CustomerRepo customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    //회원가입
    public Long registerCustomer(CustomerRequestDTO dto) {
        if (customerRepository.existsByCustomerId(dto.getCustomerId())) {
            throw new IllegalArgumentException("이미 존재하는 고객 ID입니다.");
        }

        CustomerEntity customer = CustomerEntity.builder()
                .customerId(dto.getCustomerId())
                .password(passwordEncoder.encode(dto.getPassword())) // 암호화
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .customersStyle(dto.getCustomersStyle())
                .customersBackground(dto.getCustomersBackground())
                .customersType(dto.getCustomersType())
                .createdDate(LocalDateTime.now())
                .build();

        return customerRepository.save(customer).getId();
    }

    //로그인
    public CustomerLoginResponseDTO login(CustomerLoginRequestDTO dto) {
        CustomerEntity customer = customerRepository.findByCustomerId(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객 ID입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), customer.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.generateToken(customer.getCustomerId());

        return CustomerLoginResponseDTO.builder()
                .userName(customer.getNickname() != null ? customer.getNickname() : customer.getCustomerId())
                .accessToken(token)
                .build();
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        CustomerEntity customer = customerRepository.findByCustomerId(String.valueOf(id))
                .orElseThrow(() -> new IllegalArgumentException("해당 고객을 찾을 수 없습니다."));

        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .customerId(customer.getCustomerId())
                .email(customer.getEmail())
                .nickname(customer.getNickname())
                .customersStyle(customer.getCustomersStyle())
                .customersBackground(customer.getCustomersBackground())
                .customersType(customer.getCustomersType())
                .build();
    }



}
