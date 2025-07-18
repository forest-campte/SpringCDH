package com.Campmate.DYCampmate.jwt;


import com.Campmate.DYCampmate.JwtUtil;
import com.Campmate.DYCampmate.entity.CustomerEntity;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements Filter {
    private final JwtUtil jwtUtil;
    private final CustomerRepo customerRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        String authHeader = httpReq.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // "Bearer " 이후 토큰

            if (jwtUtil.validateToken(token)) {
                String customerId = jwtUtil.getCustomerIdFromToken(token);
                CustomerEntity customer = customerRepository.findByCustomerId(customerId).orElse(null);

                if (customer != null) {
                    request.setAttribute("customer", customer); // 인증 정보 저장
                }
            }
        }

        chain.doFilter(request, response);
    }

}
