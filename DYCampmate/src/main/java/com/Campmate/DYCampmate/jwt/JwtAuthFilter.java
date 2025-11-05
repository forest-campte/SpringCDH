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

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    private final AdminRepo adminRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                /**
                String customerId = jwtUtil.getCustomerIdFromToken(token);

                // customerId를 사용하여 DB에서 사용자 정보를 조회합니다.
                // 여기서는 간단히 User 객체를 생성하지만, UserDetails를 구현한 클래스를 사용하는 것이 더 좋습니다.
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(customerId);

                // 1. 인증 객체 생성
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, // Principal (인증된 사용자 정보)
                        null, // Credentials (비밀번호, 보통 null 처리)
                        userDetails.getAuthorities() // Authorities (권한 목록)
                );

                // 2. SecurityContextHolder에 인증 정보 저장
                // 이렇게 해야 스프링 시큐리티가 현재 사용자를 인식할 수 있습니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
                 */

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