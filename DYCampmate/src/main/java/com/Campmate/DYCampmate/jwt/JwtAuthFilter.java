package com.Campmate.DYCampmate.jwt;


import com.Campmate.DYCampmate.JwtUtil;
import com.Campmate.DYCampmate.config.CustomUserDetailsService;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.repository.AdminRepo;
import com.Campmate.DYCampmate.repository.CustomerRepo;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    private final AdminRepo adminRepository;

    // 인증 제외 경로 목록 (permitAll() 경로와 동일하게)
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/admins/signup",
            "/api/admins/login",
            "/customer/signup",
            "/customer/login",
            "/api/customer/social",
            "/customer/forecast"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

//        String requestURI = request.getRequestURI();
//        // 인증 제외 경로는 바로 통과
//        if (EXCLUDED_PATHS.stream().anyMatch(requestURI::startsWith)) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        String authHeader = request.getHeader("Authorization");
        /**
        // 토큰이 없으면 그냥 다음 필터로 (403 금지)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // 토큰이 유효하지 않으면 인증 없이 통과 (403 X)
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 유효 → 사용자 정보 세팅
        Claims claims = jwtUtil.getClaims(token);
        UserDetails userDetails;

        if (claims.get("email") != null) {
            // Admin 처리
            String adminId = claims.getSubject();
            AdminEntity admin = adminRepository.findById(Long.parseLong(adminId))
                    .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

            userDetails = new User(
                    admin.getId().toString(),
                    admin.getPassword(),
                    Collections.singletonList(() -> "ROLE_ADMIN")
            );
        } else {
            // Customer 처리
            String customerId = claims.getSubject();
            userDetails = customUserDetailsService.loadUserByUsername(customerId);
        }

        // 인증 객체 등록
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
     **/
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                // === [로직 수정] ===
                Claims claims = jwtUtil.getClaims(token); // 1. 모든 클레임 추출
                UserDetails userDetails;

                // 2. 'email' 클레임이 있는지 확인
                if (claims.get("email") != null) {
                    // (1) Admin 사용자 처리
                    String adminId = claims.getSubject();
                    AdminEntity admin = adminRepository.findById(Long.parseLong(adminId))
                            .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

                    // AdminEntity로 UserDetails 생성
                    userDetails = new User(
                            admin.getId().toString(), // UserDetails의 username에 ID 저장
                            admin.getPassword(),
                            Collections.singletonList(() -> "ROLE_ADMIN") // Admin 권한
                    );

                } else {
                    // (2) Customer 사용자 처리 (기존 로직)
                    String customerId = claims.getSubject();
                    userDetails = customUserDetailsService.loadUserByUsername(customerId);
                }

                // 3. 인증 객체 생성
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, // Principal (인증된 사용자 정보)
                        null, // Credentials
                        userDetails.getAuthorities() // Authorities
                );

                // 4. SecurityContextHolder에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response); // 다음 필터로 요청 전달

    }
}