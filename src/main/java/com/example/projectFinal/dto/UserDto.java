package com.example.projectFinal.dto;

import com.google.auto.value.AutoValue;
import jakarta.persistence.Column;
import lombok.*;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.Optional;

public class UserDto {

    @NoArgsConstructor
    @Getter

    public static class RegisterDto{
        private String userId;

        private String password;

        private String email;

        private String nickname;

    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class LoginDto{
        private String userId;

        private String password;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ResDto{
        private boolean result;

        @Nullable private String msg;

        @Nullable private String Token;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class LoginResDto{
        private boolean result;

        @Nullable private String msg;

        @Nullable private String AccessToken;

        @Nullable private String RefreshToken;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class AuthuserDto{
        private boolean result;

        @Nullable private String nickname;
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

        private String refresh_key;

        private String roomId;

    }

    @Builder
    @Data
    @AllArgsConstructor
    public static class TokenDto{
        private String accessToken;

        private String refreshToken;
    }

}
