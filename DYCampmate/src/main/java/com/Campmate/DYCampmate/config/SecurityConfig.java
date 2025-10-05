package com.Campmate.DYCampmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF, 폼 로그인, HTTP Basic 인증 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 2. 세션을 사용하지 않도록 설정 (STATELESS)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 4. 요청 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 회원가입 및 로그인 API는 누구나 접근 가능
                        .requestMatchers("/api/admins/signup", "/api/admins/login").permitAll()
                        // TODO: 추후 인증이 필요한 경로를 여기에 추가 (예: .requestMatchers("/api/zones/**").authenticated())
                        // 그 외 모든 요청은 일단 허용
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    // CORS 설정을 Bean으로 통합 관리
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // React 개발 서버 주소만 명시적으로 허용 (더 안전)
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    /// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////



//
//    // Spring Security CORS 설정 추가
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
//                );
//        return http.build();
//    }

    // CORS 설정을 Bean으로 등록
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.addAllowedOriginPattern("*"); // 모든 origin 허용 (개발 중에만!)
//        /*
//        configuration.setAllowedOrigins(List.of(
//                "http://localhost:3000",    // React 서버 주소
//                "http://175.118.115.201"    // android 요청 주소
//
//        ));
//         */
//
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }



}
