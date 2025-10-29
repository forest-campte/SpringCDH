package com.Campmate.DYCampmate.config;

import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepo customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username을 customerId로 간주하고 고객 정보 조회
        CustomerEntity customer = customerRepository.findByCustomerId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + username));

        // Spring Security의 UserDetails 객체로 변환하여 반환
        return new User(
                customer.getCustomerId(),   // principal.getName()이 될 값
                customer.getPassword(),     // 비밀번호
                Collections.singletonList(() -> "ROLE_USER") // 권한 (임시)
        );
    }
}
