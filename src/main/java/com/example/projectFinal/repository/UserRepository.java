package com.example.projectFinal.repository;

import com.example.projectFinal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserId(String email);
    boolean existsByNickname(String email);

}
