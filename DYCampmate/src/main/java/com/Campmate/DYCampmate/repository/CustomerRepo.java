package com.Campmate.DYCampmate.repository;

import com.Campmate.DYCampmate.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//JPA의 인터페이스 (DB 조회/저장 등)
//JpaRepository를 상속하면 기본 CRUD 제공됨
public interface CustomerRepo extends  JpaRepository<CustomerEntity,Long> {
    Optional<CustomerEntity> findByCustomerId(String customerId);
    boolean existsByCustomerId(String customerId);
    Optional<CustomerEntity> findByEmail(String email);
    Optional<CustomerEntity> findByEmailAndNickname(String email, String nickname);


}
