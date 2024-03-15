package com.example.projectFinal.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class UserDto {
    @Builder
    @Getter
    public static class SignUpDto{
        private String userId;

        private String password;

        private String email;

        private String nickname;
    }

}
