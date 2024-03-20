package com.example.projectFinal.repository;

import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserId(String email);
    boolean existsByNickname(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User SET refresh_key = :refreshToken WHERE userId = :userId")
    void updateRefreshToken(@Param("userId") String userId, @Param("refreshToken") String refreshToken);

    @Transactional
    @Modifying
    @Query("UPDATE User e SET e.refresh_key = null WHERE e.userId = :id")
    void RefreshTokenToNull(@Param("id") String userId);

    @Transactional
    @Query("SELECT u FROM User u WHERE u.refresh_key = :refreshToken")
    User findNicknameFromToken(@Param("refreshToken") String refreshToken);
}
