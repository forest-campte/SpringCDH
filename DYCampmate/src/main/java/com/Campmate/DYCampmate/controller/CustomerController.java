package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.*;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.service.AdminService;
import com.Campmate.DYCampmate.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
//요청 처리 + 응답 반환
//RequestDTO를 받아서 Service 호출 → ResponseDTO로 응답
public class CustomerController {
    private final CustomerService customerService;
    private final AdminService adminService;


    @PostMapping("/signup")
    public ResponseEntity<CustomerSimpleResponseDTO> registerCustomer(@RequestBody CustomerRequestDTO dto) {
        Long id = customerService.registerCustomer(dto);
        return ResponseEntity.ok(new CustomerSimpleResponseDTO(true,"회원가입 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody CustomerLoginRequestDTO dto) {
        try {
            CustomerLoginResponseDTO response = customerService.login(dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(HttpServletRequest request) {
        CustomerEntity customer = (CustomerEntity) request.getAttribute("customerId"); // customers_Id??

        if (customer == null) {
            return ResponseEntity.status(401).body("토큰이 유효하지 않거나 만료되었습니다.");
        }

        CustomerResponseDTO dto = CustomerResponseDTO.builder()
                .id(customer.getId())
                .customerId(customer.getCustomerId())
                .email(customer.getEmail())
                .nickname(customer.getNickname())
                .customersStyle(customer.getCustomersStyle())
                .customersBackground(customer.getCustomersBackground())
                .customersType(customer.getCustomersType())
                .provider(customer.getProvider())
                .build();

        return ResponseEntity.ok(dto);
    }


    //맞춤형 캠핑장 리스트 조회
    @GetMapping("admins/{customerId}")
    public ResponseEntity<List<AdminDTO>> recommendAdmins(@PathVariable Long customerId) {
        List<AdminDTO> result = adminService.recommendAdmins(customerId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/findId")
    public ResponseEntity<CustomerFindIdResponseDTO> findId(@RequestBody CustomerFindIdRequestDTO dto) {
        return ResponseEntity.ok(customerService.findCustomerId(dto.getEmail(), dto.getNickname()));
    }
}
