package com.example.projectFinal.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

public class UserDto {

    @NoArgsConstructor
    @Getter

    public static class RegisterDto{
        private String userId;

        private String password;

        private String email;

        private String nickname;

    }

    @Getter
    @NoArgsConstructor
    public static class GetUserDto{
        private String userId;

        private String password;

        private String email;

        private String nickname;

        private Timestamp createdAt;

        private Timestamp deletedAt;

        private String profileImg;

        private String refreshKey;

        private String roomId;

    }

}
