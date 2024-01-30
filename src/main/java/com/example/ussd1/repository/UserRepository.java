package com.example.ussd1.repository;

import com.example.ussd1.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
     Optional<UserEntity> findByPhoneNumber(String phoneNumber);

}
