package com.Campmate.DYCampmate.jwt;


import com.Campmate.DYCampmate.JwtUtil;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                String customerId = jwtUtil.getCustomerIdFromToken(token);

                // customerId를 사용하여 DB에서 사용자 정보를 조회합니다.
                // 여기서는 간단히 User 객체를 생성하지만, UserDetails를 구현한 클래스를 사용하는 것이 더 좋습니다.
                User user = new User(customerId, "", Collections.emptyList());

                // 1. 인증 객체 생성
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, // Principal (인증된 사용자 정보)
                        null, // Credentials (비밀번호, 보통 null 처리)
                        user.getAuthorities() // Authorities (권한 목록)
                );

                // 2. SecurityContextHolder에 인증 정보 저장
                // 이렇게 해야 스프링 시큐리티가 현재 사용자를 인식할 수 있습니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response); // 다음 필터로 요청 전달
    }
}