package com.example.projectFinal.repository;

import com.example.projectFinal.dto.UserDto;
import com.example.projectFinal.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserIdAndDeletedAtIsNotNull(String userid);

    boolean existsByNickname(String nickname);

    User findByNickname(String nickname);

    User findByUserId(String userid);

    @Modifying
    @Transactional
    @Query("UPDATE User SET refresh_key = :refreshToken WHERE userId = :userId")
    void updateRefreshToken(@Param("userId") String userId, @Param("refreshToken") String refreshToken);

    @Modifying
    @Transactional
    @Query("UPDATE User SET profileImg = :awsurl WHERE userId = :userId")
    boolean updateProfileImg(@Param("userId") String userId, @Param("awsurl") String awsurl);

    @Transactional
    @Modifying
    @Query("UPDATE User e SET e.refresh_key = null WHERE e.userId = :id")
    void RefreshTokenToNull(@Param("id") String userId);

    @Transactional
    @Query("SELECT u FROM User u WHERE u.refresh_key = :refreshToken")
    User findNicknameFromToken(@Param("refreshToken") String refreshToken);

    @Transactional
    @Modifying
    @Query("UPDATE User e SET e.password = :encryptPw WHERE e.userId = :userid")
    void updatePW(@Param("userid") String userid, @Param("encryptPw") String encryptPw);

    @Transactional
    @Modifying
    @Query("UPDATE User e SET e.email = :email WHERE e.userId = :userid")
    void updateEmail(@Param("userid") String userid, @Param("email") String email);


    @Transactional
    @Modifying
    @Query("update User u set u.deletedAt = CURRENT_TIMESTAMP where u.userId = :userId")
    void softDeleteUserById(@Param("userId") String userId);


}
