package com.Campmate.DYCampmate.controller;

import com.Campmate.DYCampmate.dto.*;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.service.AdminService;
import com.Campmate.DYCampmate.service.CustomerService;
import com.Campmate.DYCampmate.service.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
//요청 처리 + 응답 반환
//RequestDTO를 받아서 Service 호출 → ResponseDTO로 응답
public class CustomerController {
    private final CustomerService customerService;
    private final AdminService adminService;
    private final WeatherService weatherService;

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
    public ResponseEntity<?> getMyInfo(Authentication authentication) { // 3. HttpServletRequest 대신 Authentication 사용

        // 4. 인증 객체 유효성 검사
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("인증 정보가 없습니다.");
        }

        // 5. Principal(사용자 정보) 가져오기
        Object principal = authentication.getPrincipal();
        UserDetails userDetails;

        if (principal instanceof UserDetails) {
            userDetails = (UserDetails) principal;
        } else {
            // Admin 등 다른 타입의 Principal이 들어올 경우
            return ResponseEntity.status(403).body("고객 정보에 접근할 수 없습니다.");
        }

        // 6. UserDetails에서 customerId(String) 추출
        String customerIdString = userDetails.getUsername();

        // 7. customerId(String)로 실제 CustomerEntity 정보 조회
        // (CustomerService에 findCustomerByCustomerId 메소드가 필요합니다)
        CustomerEntity customer;
        try {
            customer = customerService.findCustomerByCustomerId(customerIdString);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }

        // 8. DTO 빌드 (기존 로직 동일)
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

    @GetMapping("/forecast")
    public Mono<ResponseEntity<List<WeatherDTO>>> getForecast(
            @RequestParam String lat,
            @RequestParam String lon) {

        return weatherService.getFiveDayForecast(lat, lon)
                .map(ResponseEntity::ok) // 성공 시 List<WeatherDto> 반환
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


}
