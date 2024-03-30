package com.example.projectFinal.dto;

import lombok.*;

import javax.annotation.Nullable;
import java.sql.Timestamp;

public class UserDto {

    @NoArgsConstructor
    @Data
    public static class RegisterDto{
        private String userId;

        private String password;

        private String email;

        @Nullable private String nickname;

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

        @Nullable private String UserId;

        @Nullable private String NewToken;
    }

    @Data
    @NoArgsConstructor
    public static class GetUserDto{
        private boolean result;

        private String userId;

        private String email;

        private String nickname;

        private String profileImg;

        private String roomId;
    }

    @Builder
    @Data
    @AllArgsConstructor
    public static class TokenDto{
        private String accessToken;

        @Nullable private String refreshToken;
    }

    @Data
    public static class SendChatDto{
        private String nickname;
        private String userMsg;
        private String Aimsg;
        private boolean result;
        private String emotion;
    }

    @Data
    public static class RealloginResDto{
        private boolean result;
        private String msg;
    }

    @Data
    public static class UpdateInfoDto{
        private String userid;
        private String email;
        private String inputpw;
    }

    @Data
    @NoArgsConstructor
    public static class WithdrawRequest {
        private String userId;
    }

}
