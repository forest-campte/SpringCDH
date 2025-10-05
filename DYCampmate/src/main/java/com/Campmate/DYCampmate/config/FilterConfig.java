package com.Campmate.DYCampmate.config;


import com.Campmate.DYCampmate.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;



@Configuration
public class FilterConfig {


    // 이 설정으로 /customers/me 요청 시, Authorization: Bearer <token> 헤더가 필요
//    @Bean
//    public FilterRegistrationBean<Filter> jwtFilter(JwtAuthFilter jwtAuthFilter) {
//        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(jwtAuthFilter);
//        registrationBean.addUrlPatterns("/customer/me"); // 인증 필요한 경로 등록
//        return registrationBean;
//    }


}
