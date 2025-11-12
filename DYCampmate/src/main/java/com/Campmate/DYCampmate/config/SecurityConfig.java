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
                                        "/customer/signup", "/customer/login", // (추가) 고객 회원가입/로그인 경로 허용
                                        "/api/customer/social"
                                ).permitAll()
                                // 2. 앱 + 전체관리자용 조회 API (모두 허용)
                                // --- [수정] ---
                                .requestMatchers(HttpMethod.GET,
                                        "/api/zones/**", // 기존 (앱+전체관리자)
                                        "/api/reservations/all", // [추가] (전체관리자)
                                        "/api/admins/all" // [추가] (전체관리자)
                                ).permitAll()
                                // 3. React 일반 관리자 앱에서 사용하는 인증 필요 API
                                .requestMatchers(HttpMethod.GET, "/api/admins/me").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/admins/me").authenticated()
                                .requestMatchers(
                                        HttpMethod.POST, "/api/zones", "/api/zones/form-data"
                                ).authenticated()
                                .requestMatchers(
                                        HttpMethod.PUT, "/api/zones/*", "/api/zones/*/form-data"
                                ).authenticated()
                                .requestMatchers(
                                        HttpMethod.DELETE, "/api/zones/*"
                                ).authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/reservations/admin/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/reservations/make").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/reservations/customer/**").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/reservations/*/cancel").authenticated()

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