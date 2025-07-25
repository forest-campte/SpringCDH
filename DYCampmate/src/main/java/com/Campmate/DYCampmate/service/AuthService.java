package com.Campmate.DYCampmate.service;


import com.Campmate.DYCampmate.dto.LoginRequestDTO;
import com.Campmate.DYCampmate.entity.AdminEntity;
import com.Campmate.DYCampmate.repository.AdminRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


    private final AdminRepo adminRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AdminRepo adminRepo, PasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminEntity authenticate(LoginRequestDTO request){
        AdminEntity admin = adminRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일이 존재하지 않습니다."));

        if(!passwordEncoder.matches(request.getPassword(), admin.getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }

        return admin;
    }

}
