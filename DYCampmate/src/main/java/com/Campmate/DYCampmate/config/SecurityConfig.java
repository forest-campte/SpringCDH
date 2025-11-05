package com.Campmate.DYCampmate.config;

import com.Campmate.DYCampmate.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))


                .authorizeHttpRequests(auth -> auth
                                // 1. (수정) 관리자 및 고객의 로그인, 회원가입은 무조건 허용
                                .requestMatchers(
                                        "/api/admins/signup", "/api/admins/login",
                                        "/customer/signup", "/customer/login" // (추가) 고객 회원가입/로그인 경로 허용
                                ).permitAll()

                                // 2. 캠핑장 상세보기/리뷰보기 (GET)는 허용
                                .requestMatchers(HttpMethod.GET, "/api/zones/**").permitAll()

                                // 3. /api/zones/** 에 대한 GET 이외의 요청(POST, PUT, DELETE 등)은 인증 필요
                                .requestMatchers("/api/zones/**").authenticated()

                                // 4. 예약 생성은 인증 필요
                                .requestMatchers(HttpMethod.POST, "/api/reservations/make").authenticated()

                                // 5. 나의 예약 목록 조회는 인증 필요
                                .requestMatchers(HttpMethod.GET, "/api/reservations/customer/**").authenticated() // (경로 수정)

                                // 6. 위에서 명시한 외의 모든 요청은 인증을 받도록 변경 (보안 강화)
                                .anyRequest().authenticated()
//                        .anyRequest().permitAll()
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}